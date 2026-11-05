# asgard

Asgard is a multi-agent brainstorming app built on Quarkus and LangChain4j. You ask it a question
or a proposal ("Should we migrate our monolith to microservices this year?"). It sends the question to a
council of Norse gods, each one wearing one of Edward de Bono's **Six Thinking Hats**. Odin, the Blue
Hat, then weighs their counsel and gives a final verdict.

The brainstorming is streamed to the client as server-sent events (SSE) while it runs, so a UI can show each god 
speaking in turn.

## The council

| God      | Hat    | Agent                                                   | Role                                                                       | Reads                        | Writes (output key)  |
|----------|--------|---------------------------------------------------------|----------------------------------------------------------------------------|------------------------------|----------------------|
| Heimdall | White  | [`HeimdallAgent`](src/main/java/io/shodo/asgard/agent/HeimdallAgent.java)             | Separates verified facts from assumptions, using Tavily web search          | `prompt`                     | `facts`              |
| Freyja   | Red    | [`FreyjaAgent`](src/main/java/io/shodo/asgard/agent/FreyjaAgent.java)   | Gut feelings and hunches, with no justification                             | `prompt`                     | `emotional`          |
| Hel      | Black  | [`HelAgent`](src/main/java/io/shodo/asgard/agent/HelAgent.java)       | Ranked risks with their causal mechanism, grounded only in Heimdall's facts | `facts`                      | `risk`               |
| Freyr    | Yellow | [`FreyrAgent`](src/main/java/io/shodo/asgard/agent/FreyrAgent.java) | The strongest honest case *for* the proposal                                | `facts`                      | `optimism`           |
| Loki     | Green  | [`LokiAgent`](src/main/java/io/shodo/asgard/agent/LokiAgent.java)     | Alternatives and provocations, designed around Hel's risks                  | `facts`, `risk`              | `creative`           |
| Odin     | Blue   | [`OdinAgent`](src/main/java/io/shodo/asgard/agent/OdinAgent.java) | Picks the two ideas to try first                                        | all five outputs above       | `answer`             |

Each agent is a declarative LangChain4j `@Agent` interface: the prompt lives in `@UserMessage`, its
inputs are `@V` parameters and its result is written to the shared agentic scope under `outputKey`.
The prompts enforce hat discipline (for example Hel and Freyr may not introduce any fact that Heimdall
did not report).

## Architecture

```
  HTTP client / asgard-react
        │  POST /api/thing/storm  {"question": "..."}
        ▼
┌──────────────────────┐   SSE: start, token, end, ..., done
│ ThingResource        │──────────────────────────────────────────────▶ client
└─────────┬────────────┘                         ▲
          ▼                                      │ emit(ThingEvent)
┌──────────────────────┐   open / close   ┌──────┴───────┐
│ Thing                │─────────────────▶│ ThingStreams │ one emitter per debateId
│  - runs the session  │                  └──────▲───────┘
│                      │                         │
│    on a worker thread│                         │ GodListener (one per agent)
└─────────┬────────────┘                         │ before / after / error
          ▼                                      │
┌──────────────────────────────────────────────────────────────┐
│ SixHatsSession  (@PlannerAgent, outputKey = "answer")        │
│   GoalOrientedPlanner ── GoalOrientedSearchGraph ── A*       │
│                                                              │
│  prompt ─▶ Heimdall ─▶ facts ─┬─▶ Hel ─▶ risk ─┐             │
│        │                      ├─▶ Freyr ─▶ optimism          │
│        │                      └──────────▶ Loki ─▶ creative  │
│        └─▶ Freyja ─▶ emotional                               │
│                                   all five ─▶ Odin ─▶ answer │
└──────────────────────────────────────────────────────────────┘
          │ LLM calls (OpenAI by default), Tavily web search, Langfuse tracing
          ▼
```

### Packages

- **`agent`**: the six agent interfaces, their structured outputs (`AgentAnswer`,
  `FactAgentAnswer`) and `SixHatsSession`, the entry point that groups them.
  - `God` maps each god to its hat colour; the ids match `GodId` in the React app.
- **`planner`**: the planner that decides in which order the agents run.
  - `GoalOrientedSearchGraph` builds a dependency graph from the agents themselves: each `@V` argument is
    an input node and each `outputKey` an output node, with the agent as the edge between them.
  - `DependencyGraphSearch` is an A* search adapted to dependency graphs: a node can only be activated
    once *all* of its inputs are available.
  - `GoalOrientedPlanner` implements LangChain4j's `Planner`. It starts from the keys already in the scope
    (`prompt`, `debateId`), searches a path to the goal (`answer`) and calls the agents one after the
    other. Adding a new hat is just adding an agent whose inputs and output fit in the graph; no workflow
    is written by hand.
- **`service`**: the debate (in Norse myth, the *Thing* is the assembly where matters are decided).
  - `Debate` holds the state and metrics of one debate.
  - `Thing` orchestrates a debate: it emits `start`, then runs
    `SixHatsSession` on a worker thread while the agents' turns are streamed live, and finally `done`.
  - `ThingStreams` keeps one SSE emitter per running debate, and the gods that have taken the floor in it.
    Agents find theirs through the `debateId` stored in the agentic scope. Outside a debate (for example in a
    test) it does nothing.
  - `GodListener` is the `AgentListener` attached to each agent with `@AgentListenerSupplier`.
    It turns an invocation into `start` (the turn number in the planner's order, and the gods whose output the
    agent reads), `token` (the whole answer) and `end` events, with duration, token usage, the questions the god
    asks back and Heimdall's sources.
- **`api`**: `ThingResource` exposes the endpoints, `ThingEvent` is the event payload.
- **`trace`**: the agent graph of each debate and the time and tokens of its calls, for the trace page of
  `../asgard-react`. Prompts, RAG content and logs are in Langfuse. Nothing in the agents knows about it.
  - `service.DebateTraces` keeps the traces of the last 20 debates in memory. `Thing` opens a trace when a
    debate starts and closes it when the session ends.
  - `service.TraceAgentListener` is a CDI `AgentListener`: Quarkus attaches it to every agent. It records the
    agent graph (the planner's sub-agents), each turn and its tool runs.
  - `service.TraceChatModelListener` is a CDI `ChatModelListener`: it records the model, tokens and timing of each
    chat completion for the turn running on the same thread.
  - `service.TracedRetrieval` produces the agents' `RetrievalAugmentor`, built like Easy RAG's own (Easy RAG backs
    off when the application provides one), with listeners on the embedding model and the pgvector retriever.
  - `api.TraceResource` exposes them, `api.DebateTrace` is the payload.
- **`error`**: `ApiErrorMappers` turns every exception into an RFC 9457 `application/problem+json`
  response (`ApiError`) with HAL-style `_links` telling the client what it can do next.

### Streaming protocol

`POST /api/thing/storm` returns `text/event-stream`. The SSE event name is the `type` of the
`ThingEvent`:

| type    | meaning                                                                  |
|---------|--------------------------------------------------------------------------|
| `start` | without `god`: the debate opens (`debateId`, `question`); with `god`: that god's turn opens (`hat`, `order`, `heard`) |
| `token` | a chunk of the current god's text                                        |
| `end`   | the god's turn closes (`durationMs`, `tokensIn`, `tokensOut`, and when there are some, `sources`) |
| `done`  | the debate is over (total `durationMs`, `calls`, tokens)                 |
| `error` | something failed (`text`)                                                |

Examples are in [`http/thing.http`](http/thing.http).

### Another round

After Odin's picks, the mortal can push one idea further: `POST /api/thing/storm` again, with the round number,
Odin's last picks and what to push. The gods hear the question, the picks and the push.
Each round is a debate of its own, with its own id and trace, on the same question.
`picks` and `push` are required from round 2 on.

```json
{ "question": "...", "round": 2, "picks": "Odin's last picks", "push": "the idea to take further" }
```

The response is `{ "god", "answer", "durationMs" }`, plus `questions` and `sources` when there are some.

### Traces

| Endpoint                            | Returns                                                                      |
|-------------------------------------|------------------------------------------------------------------------------|
| `GET /api/thing/traces`             | The last debates, newest first (`debateId`, `question`, `status`, duration) |
| `GET /api/thing/traces/{debateId}`  | The whole trace (404 `problem+json` when unknown)                            |

A trace holds the agent graph, the turns in run order, and every call each turn made: `embedding`, `retrieval`
(pgvector search), `chat` and `tool`. `atMs` is measured from the moment the agents start. It also holds the warnings
logged during the run and the runtime (`forge`: versions, models, boot log, installed features). While the debate
runs, `status` is `running` and the trace holds what has happened so far. Traces live in memory only.

## Configuration

Settings are in [`application.properties`](src/main/resources/application.properties):

| Variable              | Purpose                                                         |
|-----------------------|-----------------------------------------------------------------|
| `OPEN_AI_API_KEY`     | Chat model of the agents and embeddings (required)              |
| `TAVILY_API_KEY`      | Web search tool used by Heimdall (required)                     |
| `XAI_API_KEY`         | Grok model of Loki, via the OpenAI-compatible xAI API (required) |
| `ANTHROPIC_API_KEY`   | Optional, the Anthropic extension is on the classpath           |
| `LANGFUSE_*`          | Langfuse URL and keys, defaults match `../docker-compose.yml`   |
| `ASGARD_DOCS_PATH`    | Folder ingested by Easy RAG at startup when the store is empty (default `documents`) |
| `ASGARD_DB_*`         | pgvector name, user and password (default `asgard`), match `../docker-compose.yml` |

To ingest again after changing the documents, start once with `QUARKUS_LANGCHAIN4J_PGVECTOR_DROP_TABLE_FIRST=true`.

## Running

Start the local infrastructure (pgvector for RAG embeddings, and self-hosted Langfuse for LLM tracing at
<http://localhost:3000>) from the repository root:

```shell script
docker compose up -d
```

Then start the app in dev mode (Java 25 is required):

```shell script
./mvnw quarkus:dev
```

The Dev UI is available at <http://localhost:8080/q/dev/>.

At each startup Easy RAG rebuilds the embeddings from the documents folder into `public.embeddings` of the
compose database (`jdbc:postgresql://localhost:5432/asgard`, user and password `asgard`), which any SQL client
(IntelliJ Database tool, DBeaver, pgAdmin) can open. The tests keep their own Dev Services container.

### Tests

The tests in `src/test` are integration tests: they call the real models, one per agent, plus one for
the whole session. They need the API keys above.

```shell script
./mvnw verify
```

### Packaging

```shell script
./mvnw package                 # target/quarkus-app/quarkus-run.jar
./mvnw package -Dnative        # native executable (GraalVM, or add -Dquarkus.native.container-build=true)
```

## Related guides

- [Quarkus LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html) (Agentic, OpenAI, Anthropic, MCP, Tavily, pgvector)
- [Easy RAG](https://docs.quarkiverse.io/quarkus-langchain4j/dev/rag-easy-rag.html)
- [Quarkus Langfuse](https://docs.quarkiverse.io/quarkus-langfuse/dev/index.html)
- [Six Thinking Hats](../how_six_hats_works.md)

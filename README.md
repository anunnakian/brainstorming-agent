# Asgard: brainstorming agent

A multi-agent brainstorming app. A council of Norse gods, each wearing one of Edward de Bono's Six Thinking Hats,
debates your question, and Odin gives the verdict.

| Folder | Contents | Details |
|---|---|---|
| [`asgard-quarkus`](asgard-quarkus) | Back end: Quarkus and LangChain4j | [README](asgard-quarkus/README.md) |
| [`asgard-react`](asgard-react) | Front end: React, Vite and TypeScript | [README](asgard-react/README.md) |
| [`docker-compose.yml`](docker-compose.yml) | Local infrastructure: pgvector and Langfuse | See [below](#run-docker-compose) |

## Prerequisites

- Java 25
- Node.js 20.19+ or 22.12+
- Docker with Docker Compose
- OpenAI, Anthropic, Tavily and Grok API keys

Maven doesn't need to be installed: the project includes the Maven wrapper.

## Run Docker Compose

From the repository root:

```shell
docker compose up -d      # start everything in the background
docker compose ps         # check the status of the services
docker compose logs -f    # follow the logs
docker compose down       # stop everything and keep the data
docker compose down -v    # stop everything and delete the data
```

| Service | Address | Credentials (dev defaults) |
|---|---|---|
| pgvector | `jdbc:postgresql://localhost:5432/asgard` | `asgard` / `asgard` |
| Langfuse UI | <http://localhost:3000> | `admin@asgard.local` / `valhalla123` |

Each default can be overridden from a `.env` file in the repository root. See
[`docker-compose.yml`](docker-compose.yml) for the variable names.

## Run the Quarkus app

1. Create `asgard-quarkus/.env` with your API keys. This file is ignored by Git.

   ```properties
   OPEN_AI_API_KEY=your-openai-key
   ANTHROPIC_API_KEY=your-anthropic-key
   TAVILY_API_KEY=your-tavily-key
   GROK_API_KEY=your-grok-key
   ```

2. Start the app in dev mode:

   ```shell
   cd asgard-quarkus
   ./mvnw quarkus:dev       # macOS, Linux, Git Bash, PowerShell
   mvnw.cmd quarkus:dev     # Windows cmd
   ```

The app runs on <http://localhost:8080>, and the Dev UI is at <http://localhost:8080/q/dev/>.

## Run the front end

With the Quarkus app running, open a second terminal:

```shell
cd asgard-react
npm install
npm run dev
```

Open <http://localhost:5174>. Vite forwards `/api` requests to Quarkus on port 8080.

## Useful links

- [LangChain4j on GitHub](https://github.com/langchain4j/langchain4j)
- [LangChain4j documentation](https://docs.langchain4j.dev/)
- [Quarkus LangChain4j documentation](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)

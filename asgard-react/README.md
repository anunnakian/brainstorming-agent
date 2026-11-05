# Asgard: the Thing (React)

The Norse council as a React app. Six gods debate the user's question: they stream from the Quarkus
`/api/thing` endpoints, and each god's mouth moves while their words arrive.

Vite, React 19 and TypeScript strict. The components in `src/asgard/` have no dependencies besides React.

## Start the front

1. Start the back end (see `../asgard-quarkus/README.md`). It must answer on `http://localhost:8080/api/thing`:

   ```bash
   docker compose up -d                 # from the repo root: pgvector + Langfuse
   cd asgard-quarkus && ./mvnw quarkus:dev
   ```

2. Install and run the front (Node 20.19+ or 22.12+):

   ```bash
   cd asgard-react
   npm install
   npm run dev
   ```

   Open http://localhost:5173. Vite proxies `/api` to `http://localhost:8080` (`vite.config.ts`).

| Script | |
|---|---|
| `npm run dev` | Dev server with hot reload on port 5173 |
| `npm run build` | Typecheck, then build to `dist/` |
| `npm run typecheck` | Typecheck only |
| `npm test` | Unit tests (Vitest) for the trace derivations and the routes |
| `npm run preview` | Serve the `dist/` build |

## The conversation page

`src/App.tsx` renders `<AsgardThing>`, the conversation page. It follows the static transcript in
`six-hats-debate.html`:

- **Header**: the question before the Thing, who holds the floor ("Round N complete" at the end of a round), the
  `Debate | Behind the scenes` switch and **New question**.
- **Council arc**: the six gods with their order of speech in the current round, the planner's as it calls them;
  the speaker glows and talks.
- **Brainstorm floor**: your question, then one tablet per turn, the answer rendered as Markdown and typed at a
  speaking pace, Heimdall's sources, and a meta line (model, time, duration, tokens). Each new round opens with a
  "Round N: …" divider; earlier rounds stay on the floor.
- **Odin's runestone**: the order of speech in the current round, with each god's duration (click to jump to the turn).
- **Composer**: bring your question before the Thing. After Odin's picks, say which idea the gods should
  push further and press **Another round**; **New question** ends the brainstorm and clears the floor.

Everything comes from the `start` / `token` / `end` / `done` events of `ThingResource`. **Behind the scenes**
opens the trace of the current debate (or of the latest one) in the same browser tab. The conversation stays mounted
meanwhile, so a debate keeps streaming while you look at its trace, and **Debate** brings you back to it.

## The trace page

`#/trace` shows the latest debate, `#/trace/<debateId>` one debate, and `?tab=<tab>` opens a tab (`agents`, `wire`,
`rag`, `timeline`, `logs`, `findings`), so a reload keeps the debate and the tab. `<AsgardTrace>` is filled from
`GET /api/thing/traces/{debateId}` (fetched again every second while the debate runs, so every tab follows it live).

Under the header, one line of totals (wall time, HTTP calls, chat and embedding tokens, the share of time waiting for
chat, and the errors or warnings when there are any), then one tab per subject:

- **Agents**: the agent graph from the planner's sub-agents (who reads which scope key, who writes it, the goal). Drag
  any node, or move it with the arrow keys; the layout is remembered in the browser (**Reset layout** forgets it). A god
  whose turn failed is ringed in red. Click a god to open their turn on the wire. **How the planner picks the order**
  (collapsed) explains how `GoalOrientedPlanner` orders the agents, with the path the run used.
- **On the wire**: one turn as a sequence diagram. Pick a god; each lane is a service the turn called (the Quarkus app,
  the model provider, pgvector, each tool), and each call is an arrow out and an arrow back, with its result. Open an
  arrow for the drawer: the prompt and its anatomy (template, injected answers, what LangChain4j appended), the request
  and response JSON side by side, the segments RAG found, or the tool's result. ↑/↓ move between arrows, ←/→ between
  gods. Opening an arrow pins the turn, so a live debate doesn't move it away.
- **Tools & RAG**: every pgvector search of the debate (with the segments found: document, score, text) and every tool
  run (arguments, result). **Show on the wire** opens the call.
- **Timeline**: every embedding call, pgvector search, chat completion and tool run, plus the warnings logged during the
  run; **What if** replays the same turns scheduled by their data dependencies.
- **Errors & logs**: its badge counts the problems (red for errors, amber for warnings only). Failed calls, failed turns
  and a failed debate first, each with **Show on the wire**, then what was logged during the run, then the server: the
  startup log, the installed features and the runtime.
- **Findings**: what the numbers of the trace reveal.

Traces live in the Quarkus memory (the last 20 debates). The picker in the header switches between them.

## Files

```
index.html, vite.config.ts   Vite entry and the /api proxy
src/main.tsx, App.tsx        mounts the pages: #/ the conversation, #/trace[/<debateId>][?tab=] the trace
src/route.ts                 parses and writes those routes
src/asgard/
  AsgardThing.tsx   the conversation page: council arc, debate floor, composer, Odin's runestone
  useThing.ts       the SSE stream + typewriter + speaker state + turn metadata (use it alone for a custom UI)
  AsgardTrace.tsx   the trace page: header, totals strip, tab bar, one panel per tab
  TraceTabBar.tsx   the tabs and the error badge; traceTabs.ts lists them
  TraceAgents.tsx   Agents: the graph and the planner
  TraceGraph.tsx    the draggable agent graph
  TraceSequence.tsx On the wire: the sequence diagram of one turn
  TraceCallDetail.tsx  what a drawer shows: the prompt, the request and response JSON, the segments, the tool result
  TraceToolsRag.tsx Tools & RAG
  TraceTimeline.tsx Timeline
  TraceLogs.tsx     Errors & logs
  TraceFindings.tsx Findings
  DebatePicker.tsx  the list of recent debates in the header
  controls.tsx      the header controls shared by both pages: the page switch, the live status (controls.css)
  useTrace.ts       the trace types and the hook that loads (and polls) a trace
  traceInsights.ts  totals, the dependency schedule, the timeline axis and the problems, computed from a trace
  traceWire.ts      the lanes and arrows of a turn
  traceFormat.ts, traceParts.tsx  number formats and small shared pieces
  Markdown.tsx      a small Markdown renderer for the gods' answers (no HTML, no dependency)
  Portrait.tsx      the six illustrated gods, with talking/blinking animations
  gods.ts           names, roles, hats, colours, seat and speech order
  asgard.css        conversation styles, all scoped under .asgard (imports the Google Fonts it needs)
  trace.css         trace styles, all scoped under .asgard-trace
  index.ts          public exports
  *.test.ts, testFixtures.ts  the unit tests and their sample traces
six-hats-debate.html              static design reference for the conversation page
six-hats-behind-the-scenes.html   static design reference for the trace page (its content; the layout is now tabs)
```

`src/asgard/` can still be copied into another app on its own.

## Use it

```tsx
import { AsgardThing } from './asgard';

export default function BrainstormPage() {
  return <AsgardThing apiBase="/api/thing" />;
}
```

| Prop | Default | |
|---|---|---|
| `apiBase` | `/api/thing` | Base URL of `ThingResource` |
| `charsPerSecond` | `38` | Speaking pace |
| `pauseMs` | `550` | Silence between speakers |
| `hideLedger` | `false` | Hide the side panel (it also hides itself below 1080px) |
| `traceHref` | | `(debateId?) => url` of the trace page (no id before the first debate); shows the **Behind the scenes** link |
| `className`, `style` | | The root fills `100dvh`; pass `style={{ height: '100%' }}` to fit a parent instead |

```tsx
import { AsgardTrace } from './asgard';

export function TracePage({ id }: { id?: string }) {
  return <AsgardTrace apiBase="/api/thing" debateId={id} conversationHref="#/" traceHref={(d) => `#/trace/${d}`} />;
}
```

| Prop | Default | |
|---|---|---|
| `apiBase` | `/api/thing` | Base URL of `ThingResource` (traces are under `/traces`) |
| `debateId` | the latest | The debate to show |
| `pollMs` | `1000` | How often a running debate is fetched again |
| `conversationHref` | | Link back to the conversation page |
| `traceHref` | | `(debateId, tab?) => url`; enables the picker of recent debates |
| `tab` | `agents` | The tab to show |
| `onTabChange` | | `(tab) => void` when the reader picks a tab, e.g. to keep it in the URL |

## Talk to Quarkus

In development, Vite proxies `/api` to Quarkus so the browser stays on one origin.

If the front end and Quarkus run on different origins in production, enable CORS on Quarkus:
`quarkus.http.cors.enabled=true` and `quarkus.http.cors.origins=https://your-front.example`.

Streaming uses `fetch` and parses the SSE frames, so `POST /storm` works (EventSource only does GET).

## Build your own UI

```tsx
const thing = useThing({ apiBase: '/api/thing' });
thing.start('Should I launch a free tier?');     // every god speaks, Odin gives the verdict
thing.reset();

thing.turns       // lines on the floor: { speaker, text, shown, ended, info?, stats? }; show text.slice(0, shown)
                  // info: order, heard, model, startedAt, durationMs, tokens, questions, sources;
                  // stats: on the 'closing' turn after `done`
thing.speaker     // god holding the floor
thing.talking     // true while that god still has words to type: pass it to <Portrait talking />
thing.busy        // a request is streaming
```

`<Portrait god="loki" talking />` fills its parent box. Give it a sized, round container.

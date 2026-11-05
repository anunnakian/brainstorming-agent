import { AsgardThing } from './asgard';

/** The conversation page. 45 chars/s matches the pace of the server's token frames. */
export function App() {
  return <AsgardThing apiBase="/api/thing" charsPerSecond={45} />;
}

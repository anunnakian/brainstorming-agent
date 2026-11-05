import { useEffect, useRef, useState, type CSSProperties, type FormEvent } from 'react';
import { LiveStatus, PlusIcon } from './controls';
import { CHAIR, GODS, SEAT_ORDER, SPEECH_ORDER, SURTR, isGod, type GodId } from './gods';
import { Markdown } from './Markdown';
import { Portrait } from './Portrait';
import { useThing, type Turn, type UseThingOptions } from './useThing';
import './asgard.css';

export interface AsgardThingProps extends UseThingOptions {
  className?: string;
  style?: CSSProperties;
  /** Hide Odin's runestone (the side panel). Below 1080px it moves under the debate. */
  hideLedger?: boolean;
}

/** A god's turn that has been fully spoken, with what the server told us about it. */
type SpokenTurn = Turn & { speaker: GodId };

const paint = (god: GodId) => ({ '--c': GODS[god].color, '--aura': GODS[god].aura }) as CSSProperties;
const seconds = (ms: number) => `${(ms / 1000).toFixed(1)} s`;
const count = (n: number) => n.toLocaleString('en');
const clock = (iso: string) => new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });

const scrollToTurn = (id: number) => {
  const reduced = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
  document.getElementById(`turn-${id}`)?.scrollIntoView({ behavior: reduced ? 'auto' : 'smooth', block: 'start' });
};

/** The conversation page: council arc, debate floor, composer and Odin's runestone. */
export function AsgardThing({ className, style, hideLedger = false, ...options }: AsgardThingProps) {
  const thing = useThing(options);
  const { question, turns, currentId, speaker, talking, spoken, busy, announcement, round, roundStart } = thing;
  /** The Thing has gathered on the question: one question, one debate. */
  const gathered = question !== null;
  const [draft, setDraft] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);
  const floorRef = useRef<HTMLElement>(null);

  // keep the newest words in view, unless the reader scrolled up. Only scrolling decides it: a new tablet can add more
  // than a screen at once, and a hidden floor (while the trace shows) measures 0.
  const stuck = useRef(true);
  const onFloorScroll = () => {
    const floor = floorRef.current;
    if (floor && floor.clientHeight > 0) stuck.current = floor.scrollHeight - floor.scrollTop - floor.clientHeight < 160;
  };
  const lastTurn = turns[turns.length - 1];
  useEffect(() => {
    const floor = floorRef.current;
    if (floor && stuck.current) floor.scrollTop = floor.scrollHeight;
  }, [turns.length, lastTurn?.shown]);
  // back from the trace, the floor gets its size again: catch up with what was said meanwhile
  useEffect(() => {
    const floor = floorRef.current;
    if (!floor || typeof ResizeObserver === 'undefined') return;
    const observer = new ResizeObserver(() => { if (stuck.current) floor.scrollTop = floor.scrollHeight; });
    observer.observe(floor);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    if (!busy) inputRef.current?.focus();
  }, [busy]);

  const submit = (event: FormEvent) => {
    event.preventDefault();
    const text = draft.trim();
    if (!text || busy) return;
    if (gathered) {
      // after Odin's harvest, the founder says what the gods should push further
      if (!canPush) return;
      setDraft('');
      void thing.nextRound(text);
      return;
    }
    setDraft('');
    void thing.start(text);
  };

  const newQuestion = () => {
    stuck.current = true;
    thing.reset();
    setDraft('');
  };

  // the runestone and the seats count the current round only; earlier rounds stay on the floor
  const thisRound = turns.filter((t) => t.id >= roundStart);
  // turns that are fully spoken (the cursor has moved past them)
  const finished = thisRound.filter((t): t is SpokenTurn => isGod(t.speaker) && t.id !== currentId);
  const debated = finished.filter((t) => t.info);
  const closing = thisRound.find((t) => t.speaker === 'closing');
  // the planner decides who speaks when: the gods it has called come first, in that order, the others keep the default
  const called = thisRound.filter((t): t is SpokenTurn => isGod(t.speaker) && t.speaker !== CHAIR && !!t.info).map((t) => t.speaker);
  // once the harvest is in, the founder can push one idea further: the next round of the brainstorm
  const canPush = !!closing && !busy && currentId === null;
  const order = [...new Set([...called, ...SPEECH_ORDER])];

  const seat = (god: GodId) => {
    const g = GODS[god];
    const isSpeaking = speaker === god;
    const classes = ['seat', god === CHAIR ? 'seat--chair' : '', isSpeaking ? 'is-speaking' : ''].filter(Boolean).join(' ');
    return (
      <div key={god} className={classes} data-god={god} style={paint(god)}>
        <span className="seat-face">
          <span className="aura" />
          <span className="ring"><Portrait god={god} talking={isSpeaking && talking} /></span>
        </span>
        <span className="seat-name">{g.name.toUpperCase()}<Wave on={isSpeaking && talking} /></span>
        <span className="seat-role">{g.role}</span>
      </div>
    );
  };

  const rootClasses = ['asgard', speaker ? 'has-speaker' : '', className ?? ''].filter(Boolean).join(' ');
  const nowLabel = speaker
    ? <><b>{GODS[speaker].name}</b> is speaking</>
    : closing ? `Round ${round} complete` : question ? 'The Thing listens' : 'The Thing waits';

  return (
    <div className={rootClasses} style={style}>
      <header className="top">
        <div className="brand">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#BFD3DA" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M12 2v20" /><path d="M12 7 7 3M12 7l5-4" /><path d="M12 13l-5-4M12 13l5-4" /><path d="M12 19l-5-4M12 19l5-4" />
          </svg>
          <span>ASGARD</span>
        </div>
        <div className="question">
          <span className="eyebrow">THE QUESTION BEFORE THE THING</span>
          <span className="question-text">{question ? `“${question}”` : 'None yet'}</span>
        </div>
        <div className="now">
          <LiveStatus color={speaker ? GODS[speaker].color : undefined} live={!!speaker && talking}>{nowLabel}</LiveStatus>
          {question !== null && <button type="button" className="solid" onClick={newQuestion}><PlusIcon />New question</button>}
        </div>
      </header>

      <div className="body">
        <main className="chamber">
          <section className="council" aria-label="The Thing of Asgard">
            <div className="row row--chair">{seat(CHAIR)}</div>
            <div className="row row--seats">{SEAT_ORDER.map(seat)}</div>
          </section>

          <section className="floor" ref={floorRef} aria-label="The debate" onScroll={onFloorScroll}>
            <div className="floor-inner">
              {turns.length === 0 && (
                <div className="welcome">
                  <span className="welcome-title">Bring your question before the Thing</span>
                </div>
              )}
              {turns.map((turn) => (
                <Message key={turn.id} turn={turn} live={turn.id === currentId} />
              ))}
            </div>
          </section>

          <form className="composer" onSubmit={submit} autoComplete="off">
            <div className="compose-row">
              <label className="sr-only" htmlFor="asgard-say">
                {canPush ? 'What the gods should push further' : gathered ? 'The Thing is brainstorming on your question' : 'Your question for the Thing'}
              </label>
              <input id="asgard-say" ref={inputRef} value={draft} maxLength={1000} disabled={busy || (gathered && !canPush)}
                onChange={(e) => setDraft(e.target.value)}
                placeholder={canPush ? 'Which idea should the gods push further?' : gathered ? 'The gods are brainstorming…' : 'What should the Thing brainstorm on?'} />
              {gathered
                ? <>
                    {canPush && <button className="primary" type="submit" disabled={busy || !draft.trim()}>Another round</button>}
                    <button className={canPush ? 'ghost' : 'primary'} type="button" disabled={busy} onClick={newQuestion}>New question</button>
                  </>
                : <button className="primary" type="submit" disabled={busy}>Ask</button>}
            </div>
          </form>
        </main>

        {!hideLedger && (
          <Ledger order={order} speaker={speaker} talking={talking} spoken={spoken} debated={debated} />
        )}
      </div>

      <footer className="disclaimer" role="note">
        <b>Responses are generated by AI.</b> They may contain errors and are not professional advice.
      </footer>

      <div className="sr-only" aria-live="polite">{announcement}</div>
    </div>
  );
}

function Wave({ on }: { on: boolean }) {
  return <span className={on ? 'wave on' : 'wave'} aria-hidden="true"><i /><i /><i /><i /></span>;
}

interface MessageProps {
  turn: Turn;
  live: boolean;
}

function Message({ turn, live }: MessageProps) {
  const shown = turn.text.slice(0, turn.shown);
  const typing = live && (turn.shown < turn.text.length || !turn.ended);

  if (turn.speaker === 'you') {
    return (
      <div className="msg msg--you">
        <div className="bubble"><span className="bubble-k">You</span>{shown}</div>
      </div>
    );
  }
  if (turn.speaker === 'system') {
    return <div className="msg msg--system"><div className="note note--error">{shown}</div></div>;
  }
  if (turn.speaker === 'surtr') {
    const vars = { '--accent': SURTR.color, '--label': SURTR.label, '--c': SURTR.color, '--aura': SURTR.aura } as CSSProperties;
    return (
      <article id={`turn-${turn.id}`} className="msg msg--surtr msg--dark" style={vars}>
        <span className="msg-face"><Portrait god="surtr" talking={live && turn.shown < turn.text.length} /></span>
        <div className="msg-col">
          <div className="tablet">
            <div className="tablet-head">
              <span className="tablet-label">{SURTR.name} - {SURTR.role}</span>
            </div>
            <div className="tablet-text">
              {shown}{typing && <span className="caret" aria-hidden="true">▍</span>}
            </div>
          </div>
        </div>
      </article>
    );
  }
  if (turn.speaker === 'closing') {
    const s = turn.stats;
    return (
      <div className="closing">
        Round complete{s && ` · ${seconds(s.durationMs)} · ${s.calls} calls`}
      </div>
    );
  }
  if (turn.speaker === 'round') {
    return <div className="round-divider" role="separator"><span>{shown}</span></div>;
  }

  const god = turn.speaker;
  const g = GODS[god];
  const info = turn.info;
  const vars = { '--accent': g.accent ?? g.color, '--label': g.label, ...paint(god) } as CSSProperties;
  return (
    <>
      <article id={`turn-${turn.id}`} className={`msg msg--${god}${g.dark ? ' msg--dark' : ''}`} style={vars}>
        <span className="msg-face"><Portrait god={god} talking={live && turn.shown < turn.text.length} /></span>
        <div className="msg-col">
          <div className="tablet">
            <div className="tablet-head">
              <span className="tablet-label">{g.name.toUpperCase()}</span>
            </div>
            <div className="tablet-text">
              {turn.text.length === 0 && !turn.ended
                ? <span className="thinking" aria-label="Thinking"><i /><i /><i /></span>
                : <Markdown text={shown} tail={typing ? <span className="caret" aria-hidden="true">▍</span> : undefined} />}
            </div>
          </div>
          {info && !live && (
            <TurnNotes sources={info.sources} />
          )}
          {info && !live && (
            <div className="meta">
              {info.model && <span>{info.model}</span>}
              {info.startedAt && <span>{clock(info.startedAt)}</span>}
              {info.durationMs !== undefined && <span>{seconds(info.durationMs)}</span>}
              {info.tokensIn !== undefined && <span>{count(info.tokensIn)} in · {count(info.tokensOut ?? 0)} out</span>}
            </div>
          )}
        </div>
      </article>
    </>
  );
}

const isLink = (source: string) => /^https?:\/\//i.test(source);
/** A URL without its scheme and `www.`, short enough for a line. */
const linkLabel = (url: string) => {
  try {
    const { hostname, pathname } = new URL(url);
    return hostname.replace(/^www\./, '') + pathname.replace(/\/$/, '');
  } catch {
    return url;
  }
};

/** Where Heimdall's facts come from, once he has spoken. */
interface TurnNotesProps {
  sources?: string[];
}

function TurnNotes({ sources = [] }: TurnNotesProps) {
  if (sources.length === 0) return null;
  return (
    <div className="turn-notes">
      {sources.length > 0 && (
        <div>
          <span className="turn-notes-k">Sources</span>
          <ul>
            {sources.map((s) => (
              <li key={s}>{isLink(s) ? <a href={s} target="_blank" rel="noreferrer">{linkLabel(s)}</a> : s}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

interface LedgerProps {
  /** Order of speech: the planner's for the gods already called, the default for the others. */
  order: GodId[];
  speaker: GodId | null;
  talking: boolean;
  spoken: Partial<Record<GodId, number>>;
  /** Fully spoken turns of the started debate, in order. */
  debated: SpokenTurn[];
}

function Ledger({ order, speaker, talking, spoken, debated }: LedgerProps) {
  const lastTurn = (god: GodId) => debated.filter((t) => t.speaker === god).pop();
  const longest = Math.max(1, ...debated.map((t) => t.info?.durationMs ?? 0));

  return (
    <aside className="ledger" aria-label="Order of speech">
      <div className="ledger-head">
        <span className="ledger-title">ODIN’S RUNESTONE</span>
        <span className="ledger-sub">The order of speech, carved by the Allfather</span>
      </div>
      <ol className="ledger-list">
        {order.map((god) => {
          const isNow = speaker === god;
          const done = (spoken[god] ?? 0) > 0;
          const pending = god === CHAIR ? 'Picks last' : 'Waiting';
          const turn = done ? lastTurn(god) : undefined;
          const ms = turn?.info?.durationMs;
          const classes = ['ledger-item', isNow ? 'is-speaking' : '', done && !isNow ? 'is-spoken' : ''].filter(Boolean).join(' ');
          return (
            <li key={god} className={classes} style={paint(god)}>
              <button type="button" className="ledger-row" disabled={!turn} onClick={() => turn && scrollToTurn(turn.id)}>
                <span className="ledger-face"><Portrait god={god} talking={isNow && talking} /></span>
                <span className="ledger-name">{GODS[god].name} <span>· {god === CHAIR ? 'Picks' : GODS[god].role}</span></span>
                <span className="ledger-status">{isNow ? 'Speaking' : ms !== undefined ? seconds(ms) : done ? 'Spoken' : pending}</span>
              </button>
              {ms !== undefined && !isNow && (
                <span className="ledger-bar"><i style={{ width: `${Math.round((ms / longest) * 100)}%` }} /></span>
              )}
            </li>
          );
        })}
      </ol>
    </aside>
  );
}

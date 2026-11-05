import { useCallback, useEffect, useReducer, useRef, useState } from 'react';
import { CHAIR, GODS, SURTR, isGod, surtrLine, type GodId } from './gods';

/** Who a line belongs to: a god, the founder, a system notice, Surtr (on errors only), the end of a round,
 *  or the divider that opens the next round of the brainstorm. */
export type Speaker = GodId | 'you' | 'system' | 'surtr' | 'closing' | 'round';

/** What the server told us about a god's turn in a started debate. */
export interface TurnInfo {
  /** Position in the debate, from 1 (the first god the planner calls) to TURNS_PER_DEBATE (Odin's picks). */
  order: number;
  /** Gods whose words reached this one. Empty: the god heard only the question. */
  heard: GodId[];
  model?: string;
  startedAt?: string;
  durationMs?: number;
  tokensIn?: number;
  tokensOut?: number;
  /** Where Heimdall's facts come from: URLs or document names. */
  sources?: string[];
}

/** Totals sent with `done`. */
export interface DebateStats {
  durationMs: number;
  calls: number;
  tokensIn: number;
  tokensOut: number;
}

export interface Turn {
  id: number;
  speaker: Speaker;
  /** Everything received so far. */
  text: string;
  /** How many characters are on screen (the typewriter cursor). */
  shown: number;
  /** The server sent `end` for this turn. */
  ended: boolean;
  /** The turn has reached the floor (it waits for the previous one to finish). */
  mounted: boolean;
  /** Set on gods' turns of a started debate. */
  info?: TurnInfo;
  /** Set on the closing turn. */
  stats?: DebateStats;
}

/**
 * One event from ThingResource (`/api/thing/storm`).
 * `start` without a god opens the debate, with a god it opens that god's turn; `end` closes the turn.
 */
interface ThingEvent {
  type: 'start' | 'token' | 'end' | 'done' | 'error';
  god?: GodId | null;
  text?: string | null;
  debateId?: string;
  question?: string;
  hat?: string;
  order?: number;
  heard?: GodId[];
  model?: string;
  startedAt?: string;
  durationMs?: number;
  calls?: number;
  tokensIn?: number;
  tokensOut?: number;
  sources?: string[];
}

export interface UseThingOptions {
  /** Base URL of the Quarkus endpoints. Default `/api/thing`. */
  apiBase?: string;
  /** Speaking pace in characters per second. Default 38. */
  charsPerSecond?: number;
  /** Silence between two speakers, in ms. Default 550. */
  pauseMs?: number;
}

const prefersReducedMotion = () =>
  typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;

/**
 * Talks to the Thing over server-sent events and turns streamed tokens into speech:
 * each god's text is drained at a speaking pace, one god at a time, and `talking`
 * is true while the current god still has words to say.
 */
export function useThing({ apiBase = '/api/thing', charsPerSecond = 38, pauseMs = 550 }: UseThingOptions = {}) {
  const turnsRef = useRef<Turn[]>([]);
  const cursorRef = useRef(0);
  const nextId = useRef(1);
  const abortRef = useRef<AbortController | null>(null);
  const [, redraw] = useReducer((n: number) => n + 1, 0);

  const [question, setQuestion] = useState<string | null>(null);
  const [debateId, setDebateId] = useState<string | null>(null);
  /** Round of the brainstorm, from 1. */
  const [round, setRound] = useState(1);
  /** Id of the first turn of the current round: the runestone and the seats count from there. */
  const [roundStart, setRoundStart] = useState(0);
  const [busy, setBusy] = useState(false);
  const [speaker, setSpeaker] = useState<GodId | null>(null);
  const [talking, setTalking] = useState(false);
  const [spoken, setSpoken] = useState<Partial<Record<GodId, number>>>({});
  const [announcement, setAnnouncement] = useState('');

  // ---------- render loop: drain text like speech ----------
  useEffect(() => {
    let raf = 0;
    let last = performance.now();
    let budget = 0;
    let pauseUntil = 0;
    const reduced = prefersReducedMotion();

    const frame = (now: number) => {
      const dt = Math.min(100, now - last);
      last = now;
      const turns = turnsRef.current;
      const turn = turns[cursorRef.current];
      let changed = false;

      if (turn && now >= pauseUntil) {
        if (!turn.mounted) {
          turn.mounted = true;
          changed = true;
        }
        const remaining = turn.text.length - turn.shown;
        if (remaining > 0) {
          const speed = (!isGod(turn.speaker) && turn.speaker !== 'surtr') || reduced ? 2000 : charsPerSecond * (remaining > 160 ? 2.5 : 1);
          budget += (dt / 1000) * speed;
          const n = Math.min(remaining, Math.floor(budget));
          if (n > 0) {
            budget -= n;
            turn.shown += n;
            changed = true;
          }
        }
        const god = isGod(turn.speaker) ? turn.speaker : null;
        setSpeaker(god);
        setTalking(god !== null && turn.shown < turn.text.length);

        if (turn.ended && turn.shown >= turn.text.length) {
          if (god) {
            setSpoken((s) => ({ ...s, [god]: (s[god] ?? 0) + 1 }));
            setAnnouncement(`${GODS[god].name}: ${turn.text}`);
          } else if (turn.speaker === 'surtr') {
            setAnnouncement(`${SURTR.name}: ${turn.text}`);
          }
          cursorRef.current += 1;
          budget = 0;
          pauseUntil = now + (god ? pauseMs : 150);
          changed = true;
          if (!turns[cursorRef.current]) {
            setSpeaker(null);
            setTalking(false);
          }
        }
      }
      if (changed) redraw();
      raf = requestAnimationFrame(frame);
    };
    raf = requestAnimationFrame(frame);
    return () => cancelAnimationFrame(raf);
  }, [charsPerSecond, pauseMs]);

  // ---------- events ----------
  const push = useCallback((speaker: Speaker, text: string, ended: boolean, extra?: Pick<Turn, 'info' | 'stats'>) => {
    turnsRef.current.push({ id: nextId.current++, speaker, text, shown: 0, ended, mounted: false, ...extra });
  }, []);

  const openTurn = (god: GodId) => {
    const turns = turnsRef.current;
    for (let i = turns.length - 1; i >= 0; i--) {
      if (turns[i].speaker === god && !turns[i].ended) return turns[i];
    }
    return undefined;
  };

  const closeOpenTurns = () => turnsRef.current.forEach((t) => { t.ended = true; });

  /** Something broke: close the floor and Surtr shows up instead of the technical message, which goes to the console only. */
  const fail = useCallback((message: string) => {
    closeOpenTurns();
    console.error(message);
    push('surtr', surtrLine(), true);
  }, [push]);

  const onEvent = useCallback((e: ThingEvent) => {
    switch (e.type) {
      case 'start':
        if (e.god) {
          const info = e.order ? { order: e.order, heard: e.heard ?? [], model: e.model, startedAt: e.startedAt } : undefined;
          push(e.god, '', false, { info });
        } else if (e.debateId) {
          setDebateId(e.debateId);
        }
        break;
      case 'token': {
        const t = e.god ? openTurn(e.god) : undefined;
        if (t) t.text += e.text ?? '';
        break;
      }
      case 'end': {
        const t = e.god ? openTurn(e.god) : undefined;
        if (t) {
          t.text = t.text.trim();
          t.ended = true;
          if (t.info) {
            Object.assign(t.info, {
              durationMs: e.durationMs, tokensIn: e.tokensIn, tokensOut: e.tokensOut, sources: e.sources,
            });
          }
        }
        break;
      }
      case 'done':
        // a turn of its own, so the totals appear once Odin has finished typing
        push('closing', '', true, {
          stats: { durationMs: e.durationMs ?? 0, calls: e.calls ?? 0, tokensIn: e.tokensIn ?? 0, tokensOut: e.tokensOut ?? 0 },
        });
        break;
      case 'error':
        fail(e.text ?? 'The Thing was interrupted.');
        break;
      default:
        break;
    }
  }, [fail, push]);

  const stream = useCallback(async (url: string, init?: RequestInit) => {
    const controller = new AbortController();
    abortRef.current = controller;
    const res = await fetch(url, {
      ...init,
      signal: controller.signal,
      headers: { Accept: 'text/event-stream', ...(init?.headers ?? {}) },
    });
    if (!res.ok || !res.body) throw new Error(`The Thing refused to gather (HTTP ${res.status}).`);
    const reader = res.body.pipeThrough(new TextDecoderStream()).getReader();
    let buffer = '';
    const handle = (block: string) => {
      const data = block
        .split(/\r?\n/)
        .filter((line) => line.startsWith('data:'))
        .map((line) => line.slice(5).replace(/^ /, ''))
        .join('\n');
      if (!data) return;
      try {
        onEvent(JSON.parse(data) as ThingEvent);
      } catch (err) {
        console.warn('Unreadable Thing event', data, err);
      }
    };
    for (;;) {
      const { value, done } = await reader.read();
      if (done) break;
      buffer += value;
      let match: RegExpExecArray | null;
      while ((match = /\r?\n\r?\n/.exec(buffer))) {
        handle(buffer.slice(0, match.index));
        buffer = buffer.slice(match.index + match[0].length);
      }
    }
    if (buffer.trim()) handle(buffer);
  }, [onEvent]);

  const run = useCallback(async (task: () => Promise<void>) => {
    setBusy(true);
    try {
      await task();
    } catch (err) {
      if ((err as Error).name !== 'AbortError') fail((err as Error).message || 'The Thing could not be reached.');
    } finally {
      abortRef.current = null;
      setBusy(false);
    }
  }, [fail]);

  // ---------- actions ----------
  /** Bring a question before the Thing: every god speaks in turn, Odin closes the round with his harvest. */
  const start = useCallback((q: string) => {
    setQuestion(q);
    push('you', q, true);
    return run(() => stream(`${apiBase}/storm`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question: q }),
    }));
  }, [apiBase, push, run, stream]);

  /**
   * After Odin's picks, the founder says what the gods should push further: the next round starts on the same
   * question, with Odin's last picks and the push. Earlier rounds stay on the floor.
   */
  const nextRound = useCallback((pushed: string) => {
    const turns = turnsRef.current;
    const picks = [...turns].reverse().find((t) => t.speaker === CHAIR);
    if (question === null || !picks) return Promise.resolve();
    const next = round + 1;
    setRound(next);
    setRoundStart(nextId.current);
    setSpoken({});
    push('round', `Round ${next}: ${pushed}`, true);
    push('you', pushed, true);
    return run(() => stream(`${apiBase}/storm`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question, round: next, picks: picks.text, push: pushed }),
    }));
  }, [apiBase, push, question, round, run, stream]);

  /** Abandon the session and clear the floor. */
  const reset = useCallback(() => {
    abortRef.current?.abort();
    turnsRef.current = [];
    cursorRef.current = 0;
    setQuestion(null);
    setDebateId(null);
    setRound(1);
    setRoundStart(0);
    setSpeaker(null);
    setTalking(false);
    setSpoken({});
    setAnnouncement('');
    setBusy(false);
    redraw();
  }, []);

  useEffect(() => () => abortRef.current?.abort(), []);

  const turns = turnsRef.current.filter((t) => t.mounted);
  const current = turnsRef.current[cursorRef.current];

  return {
    question,
    /** Id of the started debate, sent with its first event. */
    debateId,
    /** Turns on the floor, oldest first. */
    turns,
    /** Id of the turn being spoken, if any. */
    currentId: current?.mounted ? current.id : null,
    /** God holding the floor (null between turns or when the founder speaks). */
    speaker,
    /** True while the speaker still has words to type: drives the mouth. */
    talking,
    /** How many times each god has finished speaking. */
    spoken,
    /** Round of the brainstorm, from 1. */
    round,
    /** Id of the first turn of the current round. */
    roundStart,
    busy,
    /** Last finished line, for an aria-live region. */
    announcement,
    start,
    nextRound,
    reset,
  };
}

export type ThingSession = ReturnType<typeof useThing>;

/** The six gods of the Thing, one per de Bono thinking hat. Ids match the Quarkus `God` enum. */
export type GodId = 'odin' | 'heimdall' | 'loki' | 'freyr' | 'hel' | 'freyja';

export interface GodMeta {
  name: string;
  role: string;
  hat: string;
  /** Main colour: names, rings, sound bars. */
  color: string;
  /** "r, g, b" used for the glowing aura. */
  aura: string;
  /** Tablet label colour (on stone, or on the dark tablets of Odin and Hel). */
  label: string;
  /** Top border of the tablet; defaults to `color`. */
  accent?: string;
  /** Dark tablet instead of the stone one. */
  dark?: boolean;
}

export const GODS: Record<GodId, GodMeta> = {
  odin:     { name: 'Odin',     role: 'Chair',                   hat: 'Blue hat',   color: '#BFD3DA', aura: '127, 168, 217', label: '#BFD3DA', dark: true },
  heimdall: { name: 'Heimdall', role: 'Prior art',               hat: 'White hat',  color: '#EEF2F0', aura: '238, 242, 240', label: '#3E4A52', accent: '#8C969C' },
  loki:     { name: 'Loki',     role: 'Remix',                   hat: 'Green hat',  color: '#6FCF8F', aura: '111, 207, 143', label: '#2C6B41', accent: '#3F9A5E' },
  freyr:    { name: 'Freyr',    role: 'Brightness and optimism', hat: 'Yellow hat', color: '#E8C25A', aura: '232, 194, 90',  label: '#7A5A12', accent: '#D9A63A' },
  hel:      { name: 'Hel',      role: 'Risks, difficulties and Problems', hat: 'Black hat',  color: '#AEB8D0', aura: '174, 184, 208', label: '#AEB8D0', dark: true },
  freyja:   { name: 'Freyja',   role: 'Ideas people love',       hat: 'Red hat',    color: '#E0685A', aura: '224, 104, 90',  label: '#8E2F2A', accent: '#C4473A' },
};

/** The chair. */
export const CHAIR: GodId = 'odin';
/** Seats around the arc, left to right (the chair sits above). */
export const SEAT_ORDER: GodId[] = ['freyr', 'loki', 'heimdall', 'freyja', 'hel'];
/**
 * Order of speech until the planner calls the gods: each turn then brings its real `order`.
 * It follows the agents' inputs (Loki remixes the other four gods' ideas). Odin gathers the harvest last.
 */
export const SPEECH_ORDER: GodId[] = ['freyja', 'heimdall', 'hel', 'freyr', 'loki', 'odin'];
/** Turns in a started debate: each god speaks once. */
export const TURNS_PER_DEBATE = SPEECH_ORDER.length;

export const isGod = (value: string): value is GodId => value in GODS;

/**
 * Surtr, the fire giant of the demo effect. Not one of the Thing: no seat, no hat, no backend.
 * He shows up on the floor only when something breaks (an HTTP 400 or 500, or a broken stream).
 */
export const SURTR = { name: 'Surtr', role: 'The Demo god', color: '#FF7A3D', aura: '255, 122, 61', label: '#FFB08A' } as const;

/** What Surtr says: one line, picked at random each time he shows up. */
export const SURTR_LINES = [
  "It doesn't work, bro. It worked this morning, I swear on Yggdrasil.",
  "Don't even re-try. I'm still here.",
  'This is called the demo effect. Applause is still accepted.',
  'Wi-Fi of the conference: 1. Speaker: 0.',
  'Ragnarök was scheduled for later. I moved it to your live demo.',
  'Somebody in the room just ran a speed test. I know who you are.',
  'Quick, everybody look at the slides, not at the screen.',
  'The gods are on a coffee break. I am the coffee break.',
  'Works on my machine. Your machine is on fire, bro.',
  'This is fine. Everything is fine. Smile at the camera.',
] as const;

export const surtrLine = () => SURTR_LINES[Math.floor(Math.random() * SURTR_LINES.length)];

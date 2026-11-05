import type { CSSProperties, ReactNode } from 'react';
import './controls.css';

/** Plain status text with a dot in `color`, and sound bars while `live`. Not interactive. */
export function LiveStatus({ color, live, children }: { color?: string; live: boolean; children: ReactNode }) {
  return (
    <span className="status" style={color ? ({ '--dot': color } as CSSProperties) : undefined}>
      <span className="status-dot" aria-hidden="true" />
      <span>{children}</span>
      <span className={live ? 'status-bars on' : 'status-bars'} aria-hidden="true"><i /><i /><i /><i /></span>
    </span>
  );
}

export function PlusIcon() {
  return (
    <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" aria-hidden="true">
      <path d="M8 3v10M3 8h10" />
    </svg>
  );
}

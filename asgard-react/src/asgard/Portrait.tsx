/* Original illustrations of the six gods of the Thing. Each one is an inline SVG.
 * .bust, .eyes and .mouth are animated by asgard.css when the portrait has the .talking class. */
import type { ReactElement } from 'react';
import type { GodId } from './gods';

const SVGS: Record<GodId, ReactElement> = {
  odin: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M32 46 C30 62 33 72 38 76 L62 76 C67 72 70 62 68 46 Z" fill="#9EA3A8"/>
      <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#2B3F63"/>
      <path d="M26 100 C32 88 40 80 50 78" fill="none" stroke="#3E5684" strokeWidth="3"/>
      <circle cx="38" cy="84" r="3.2" fill="#BFD3DA"/>
      <path d="M66 78 C67 68 75 63 82 64 L90 61 L86 67 C88 73 83 79 75 81 Z" fill="#14161C"/>
      <circle cx="82.5" cy="66.5" r="0.9" fill="#BFD3DA"/>
      <rect x="44" y="58" width="12" height="16" rx="4" fill="#B9876A"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#CF9E7E"/>
      <path d="M34 48 C34 68 42 84 50 88 C58 84 66 68 66 48 C63 56 57 60 50 60 C43 60 37 56 34 48 Z" fill="#C9CCCF"/>
      <g className="eyes" style={{ animationDelay: '0.6s' }}>
      <ellipse cx="56" cy="44" rx="1.8" ry="2.2" fill="#1C1B26"/>
      </g>
      <ellipse className="mouth" cx="50" cy="57" rx="3.4" ry="1.3" fill="#5A2320"/>
      <path d="M41 54 C45 51 55 51 59 54 C55 55 45 55 41 54 Z" fill="#C9CCCF"/>
      <path d="M33 38 L67 45" stroke="#1A1C22" strokeWidth="1.4"/>
      <ellipse cx="44" cy="44" rx="4.2" ry="3.4" fill="#1A1C22"/>
      <path d="M53 39 L60 40" stroke="#9EA3A8" strokeWidth="2.4" strokeLinecap="round" fill="none"/>
      <path d="M37 32 C37 17 44 9 50 9 C56 9 63 17 63 32 Z" fill="#4A4F5A"/>
      <ellipse cx="50" cy="32" rx="27" ry="5.5" fill="#3B3F48"/>
      </g>
      </svg>
  ),
  heimdall: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M32 44 C29 58 32 70 38 74 L62 74 C68 70 71 58 68 44 Z" fill="#E8DDBB"/>
      <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#E6E9EC"/>
      <path d="M15 91 C18 80 32 74 50 74 C68 74 82 80 85 91 L82 94 L78 97 L74 92 L70 94 L66 89 L62 91 L58 87 L54 89 L50 86 L46 89 L42 87 L38 91 L34 89 L30 94 L26 92 L22 97 L18 94 Z" fill="#96918A"/>
      <circle cx="30" cy="86" r="3.2" fill="none" stroke="#C9A24A" strokeWidth="1.6"/>
      <path d="M27 83 L34 89" stroke="#C9A24A" strokeWidth="1.2" strokeLinecap="round"/>
      <path d="M70 94 C70 78 77 67 87 62 L90 66 C82 71 77 80 77 94 Z" fill="#D9C7A0"/>
      <path d="M74.4 73.4 L81 76.6 M70.4 84.8 L78 86.3" stroke="#C9A24A" strokeWidth="1.6"/>
      <path d="M85 60 L92 68" stroke="#8C6A32" strokeWidth="2.6" strokeLinecap="round"/>
      <rect x="44" y="58" width="12" height="16" rx="4" fill="#CFA587"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#E6C2A6"/>
      <path d="M33 44 C31 29 40 22 50 22 C60 22 69 29 67 44 C63 36 57 32 50 32 C43 32 37 36 33 44 Z" fill="#E8DDBB"/>
      <path d="M34 46 C33 58 38 70 44 74 L50 76 L56 74 C62 70 67 58 66 46 C63 54 57 58.5 50 58.5 C43 58.5 37 54 34 46 Z" fill="#D6C08A"/>
      <path d="M38 60 C40 66 43 70 46 72 M62 60 C60 66 57 70 54 72" fill="none" stroke="#B89F62" strokeWidth="1" strokeLinecap="round"/>
      <path d="M45.5 72 L44.5 86 M54.5 72 L55.5 86" stroke="#D6C08A" strokeWidth="3.6" strokeLinecap="round"/>
      <path d="M45.5 72 L44.5 86 M54.5 72 L55.5 86" stroke="#B89F62" strokeWidth="3.6" strokeDasharray="0.8 2.4"/>
      <rect x="42.8" y="81" width="3.4" height="2.6" rx="0.8" fill="#C9A24A"/>
      <rect x="53.8" y="81" width="3.4" height="2.6" rx="0.8" fill="#C9A24A"/>
      <path d="M40 38.6 L47 39.8 M53 39.8 L60 38.6" stroke="#C2B07E" strokeWidth="2" strokeLinecap="round" fill="none"/>
      <g className="eyes" style={{ animationDelay: '1.7s' }}>
      <ellipse cx="44" cy="44" rx="2" ry="2.4" fill="#1C2A3A"/>
      <ellipse cx="56" cy="44" rx="2" ry="2.4" fill="#1C2A3A"/>
      </g>
      <ellipse className="mouth" cx="50" cy="56.6" rx="3.2" ry="1.2" fill="#8A3A30"/>
      <path d="M40 58 C41 52 46 51.5 50 53 C54 51.5 59 52 60 58 C57 55 53 54.4 50 55 C47 54.4 43 55 40 58 Z" fill="#D6C08A"/>
      <path d="M37 32 C28 32 20 26 22 10 C25 20 30 23 37 23 Z M63 32 C72 32 80 26 78 10 C75 20 70 23 63 23 Z" fill="#E8DFC8"/>
      <path d="M27.3 28.9 L29.6 21.3 M22.7 21.8 L25.3 17.2 M72.7 28.9 L70.4 21.3 M77.3 21.8 L74.7 17.2" stroke="#BFB08A" strokeWidth="1"/>
      <path d="M32.5 34 C31.5 21 40 13 50 13 C60 13 68.5 21 67.5 34 Z" fill="#B8C1C8"/>
      <path d="M50 13 C60 13 68.5 21 67.5 34 L50 34 Z" fill="#9DA7AF"/>
      <path d="M50 13.5 L50 33" stroke="#7F8A92" strokeWidth="3"/>
      <path d="M37.5 29 C37.8 22 41.5 17 46 15.2" fill="none" stroke="#E4EAEE" strokeWidth="1.6" strokeLinecap="round"/>
      <path d="M32.5 33.5 C40 30.5 60 30.5 67.5 33.5 L67.8 38 C60 35 40 35 32.2 38 Z" fill="#8C969C"/>
      <g fill="#C9A24A">
      <circle cx="36" cy="34.8" r="0.9"/>
      <circle cx="43" cy="33.6" r="0.9"/>
      <circle cx="57" cy="33.6" r="0.9"/>
      <circle cx="64" cy="34.8" r="0.9"/>
      </g>
      <path d="M48 34 L52 34 L51.6 49.5 C51 51 49 51 48.4 49.5 Z" fill="#A9B2B9"/>
      </g>
      </svg>
  ),
  loki: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#2A2C30"/>
      <path d="M36 74 C42 82 58 82 64 74" fill="none" stroke="#D2552E" strokeWidth="2.5"/>
      <path d="M16 94 C24 80 36 84 44 78 C52 72 62 82 70 76 C76 72 80 76 81 82" fill="none" stroke="#6E9C5E" strokeWidth="4.5" strokeLinecap="round"/>
      <ellipse cx="81" cy="83" rx="4" ry="3" fill="#6E9C5E"/>
      <circle cx="82.2" cy="82.2" r="0.8" fill="#1C1B26"/>
      <rect x="44" y="58" width="12" height="16" rx="4" fill="#C38E6C"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#DDAA88"/>
      <path d="M32 46 C28 36 30 26 36 24 C34 30 38 30 40 26 C40 18 48 14 52 16 C50 20 54 22 56 18 C60 14 68 18 66 26 C70 26 72 34 68 40 C70 44 69 46 68 46 C66 38 60 33 50 33 C40 33 35 38 32 46 Z" fill="#B8361F"/>
      <path d="M46 60 C47 66 53 66 54 60 C52 62 48 62 46 60 Z" fill="#8E2A18"/>
      <path d="M40 39.5 L47 39.5 M53 37.5 Q57 35 61 37.5" stroke="#8E2A18" strokeWidth="2" strokeLinecap="round" fill="none"/>
      <g className="eyes" style={{ animationDelay: '2.2s' }}>
      <ellipse cx="44" cy="44" rx="2" ry="1.5" fill="#1C1B26"/>
      <ellipse cx="56" cy="43.6" rx="2" ry="1.6" fill="#1C1B26"/>
      </g>
      <ellipse className="mouth" cx="50" cy="56.5" rx="3.4" ry="1.2" fill="#6B2420"/>
      <path d="M53.2 56 Q56 55.4 57.6 53.4" stroke="#6B2A20" strokeWidth="1.2" strokeLinecap="round" fill="none"/>
      </g>
      </svg>
  ),
  freyr: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M32 44 C29 58 32 70 38 74 L62 74 C68 70 71 58 68 44 Z" fill="#D9A63A"/>
      <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#6E8536"/>
      <path d="M36 74 C42 82 58 82 64 74" fill="none" stroke="#E8C25A" strokeWidth="2.5"/>
      <rect x="44" y="58" width="12" height="16" rx="4" fill="#B88660"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#D2A07A"/>
      <path d="M33 44 C31 29 40 23 50 23 C60 23 69 29 67 44 C63 36 57 32 50 32 C43 32 37 36 33 44 Z" fill="#D9A63A"/>
      <path d="M33.5 44 C33 54 34 61 37 66 Q37 70 41 70 Q41 74 45.5 73.5 Q47 77 50 76.5 Q53 77 54.5 73.5 Q59 74 59 70 Q63 70 63 66 C66 61 67 54 66.5 44 C64 53 58 59.5 50 59.5 C42 59.5 36 53 33.5 44 Z" fill="#B47A22"/>
      <path d="M39 63.5 Q41.5 65.5 44 63.5 M56 63.5 Q58.5 65.5 61 63.5 M47.5 69 Q50 71 52.5 69" fill="none" stroke="#8E5E1A" strokeWidth="1" strokeLinecap="round"/>
      <g stroke="#B08A2E" strokeWidth="1.2" strokeLinecap="round" fill="none">
      <path d="M36 34 L31 21"/>
      <path d="M42 30 L40 15"/>
      <path d="M50 29 L50 12"/>
      <path d="M58 30 L60 15"/>
      <path d="M64 34 L69 21"/>
      </g>
      <g fill="#E8C25A">
      <ellipse cx="31.5" cy="22" rx="1.8" ry="3.4" transform="rotate(-20 31.5 22)"/>
      <ellipse cx="40.2" cy="16" rx="1.8" ry="3.4" transform="rotate(-8 40.2 16)"/>
      <ellipse cx="50" cy="13" rx="1.8" ry="3.4"/>
      <ellipse cx="59.8" cy="16" rx="1.8" ry="3.4" transform="rotate(8 59.8 16)"/>
      <ellipse cx="68.5" cy="22" rx="1.8" ry="3.4" transform="rotate(20 68.5 22)"/>
      </g>
      <path d="M40 39 Q43.5 37 47 39 M53 39 Q56.5 37 60 39" stroke="#A9772A" strokeWidth="2" strokeLinecap="round" fill="none"/>
      <g className="eyes" style={{ animationDelay: '2.6s' }}>
      <ellipse cx="44" cy="44" rx="1.8" ry="2.2" fill="#1C1B26"/>
      <ellipse cx="56" cy="44" rx="1.8" ry="2.2" fill="#1C1B26"/>
      </g>
      <ellipse className="mouth" cx="50" cy="56.5" rx="3.6" ry="1.4" fill="#7A2E24"/>
      <path d="M39.5 50.5 C40 53.5 44.5 51.5 50 53 C55.5 51.5 60 53.5 60.5 50.5 C61 55 56 56.4 50 55.2 C44 56.4 39 55 39.5 50.5 Z" fill="#B47A22"/>
      </g>
      </svg>
  ),
  hel: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M24 100 C22 70 27 40 35 28 C41 18 59 18 65 28 C73 40 78 70 76 100 Z" fill="#1C1A26"/>
      <path d="M14 100 C16 84 30 76 50 76 C70 76 84 84 86 100 Z" fill="#24212F"/>
      <rect x="44" y="58" width="12" height="18" rx="4" fill="#B9B1AD"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#D9D3CF"/>
      <path d="M50 26 C59 26 66 34 66 45 C66 56 59 64 50 64 Z" fill="#3A4252"/>
      <path d="M32 50 C30 34 40 23 50 23 C60 23 70 34 68 50 C66 38 59 30 50 30 C41 30 34 38 32 50 Z" fill="#2B2838"/>
      <path d="M38 29 L41 23 L44 28 L47 21 L50 27 L53 21 L56 28 L59 23 L62 29" fill="none" stroke="#C9D6E8" strokeWidth="1.5" strokeLinejoin="round"/>
      <path d="M40 39 L47 40 M53 40 L60 39" stroke="#1C1A26" strokeWidth="1.8" strokeLinecap="round" fill="none"/>
      <g className="eyes" style={{ animationDelay: '3.4s' }}>
      <ellipse cx="44" cy="44.5" rx="1.8" ry="2" fill="#1C1B26"/>
      <ellipse cx="56" cy="44.5" rx="1.9" ry="2.1" fill="#C9D6E8"/>
      </g>
      <ellipse className="mouth" cx="50" cy="56.5" rx="3.2" ry="1.2" fill="#2A1420"/>
      </g>
      </svg>
  ),
  freyja: (
    <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
      <g className="bust">
      <path d="M32 44 C28 60 31 74 37 78 L63 78 C69 74 72 60 68 44 Z" fill="#C8642E"/>
      <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#6E4A3A"/>
      <g fill="none" stroke="#A07A5E" strokeWidth="1.4" strokeLinecap="round">
      <path d="M20 92 Q24 86 28 92"/>
      <path d="M28 86 Q32 80 36 86"/>
      <path d="M64 86 Q68 80 72 86"/>
      <path d="M72 92 Q76 86 80 92"/>
      </g>
      <rect x="44" y="58" width="12" height="16" rx="4" fill="#D29E7E"/>
      <path d="M38 74 C42 80 58 80 62 74" fill="none" stroke="#E2B44A" strokeWidth="2.6" strokeDasharray="0.1 3.2" strokeLinecap="round"/>
      <circle cx="50" cy="80.5" r="2.4" fill="#E2B44A"/>
      <ellipse cx="50" cy="45" rx="16" ry="19" fill="#E8B898"/>
      <path d="M33 44 C31 28 40 22 50 22 C60 22 69 28 67 44 C64 35 57 31 50 31 C43 31 36 35 33 44 Z" fill="#C8642E"/>
      <path d="M34 50 C30 62 30 74 34 86 C36 90 40 90 40 86 C37 74 37 62 40 52 Z" fill="#B5582A"/>
      <g stroke="#8E3F1C" strokeWidth="1.1" fill="none" strokeLinecap="round">
      <path d="M33 60 L38.5 62"/>
      <path d="M32.5 68 L38 70"/>
      <path d="M33 76 L38.5 78"/>
      </g>
      <g fill="#E07A8A">
      <circle cx="40" cy="29.5" r="2.2"/>
      <circle cx="50" cy="26.5" r="2.2"/>
      <circle cx="60" cy="29.5" r="2.2"/>
      </g>
      <circle cx="40" cy="51" r="3" fill="#E0685A" opacity="0.4"/>
      <circle cx="60" cy="51" r="3" fill="#E0685A" opacity="0.4"/>
      <path d="M40 39 Q43.5 36.5 47 38.5 M53 38.5 Q56.5 36.5 60 39" stroke="#8E3F1C" strokeWidth="1.8" strokeLinecap="round" fill="none"/>
      <g className="eyes" style={{ animationDelay: '1.1s' }}>
      <ellipse cx="44" cy="44" rx="1.8" ry="2.2" fill="#1C1B26"/>
      <ellipse cx="56" cy="44" rx="1.8" ry="2.2" fill="#1C1B26"/>
      </g>
      <ellipse className="mouth" cx="50" cy="56" rx="3.4" ry="1.4" fill="#9A3A3A"/>
      </g>
      </svg>
  ),
};

/** Surtr, the fire giant of the demo effect: charcoal skin cracked with lava, flaming hair, shades. */
const SURTR_SVG = (
  <svg className="portrait-svg" viewBox="0 0 100 100" aria-hidden="true">
    <g className="bust">
    <path d="M12 100 C14 82 30 73 50 73 C70 73 86 82 88 100 Z" fill="#2B1D1A"/>
    <path d="M24 100 L30 88 L27 82 M76 100 L70 89 L74 82 M44 100 L47 90 L43 84" fill="none" stroke="#FF7A3D" strokeWidth="1.4" strokeLinecap="round"/>
    <path d="M20 22 C24 10 30 16 32 8 C38 16 40 4 46 10 C48 2 54 6 54 0 C60 8 62 4 66 12 C70 8 74 14 72 20 C80 18 80 30 76 36 L24 36 C20 30 16 28 20 22 Z" fill="#FF7A3D"/>
    <path d="M28 30 C30 20 36 24 38 14 C42 22 46 12 50 18 C54 10 58 20 62 16 C64 24 70 22 72 30 Z" fill="#FFC14D"/>
    <rect x="44" y="58" width="12" height="16" rx="4" fill="#2E211E"/>
    <ellipse cx="50" cy="45" rx="17" ry="19" fill="#3A2A26"/>
    <path d="M36 52 L40 56 L38 62 M64 50 L60 55 L63 60 M47 28 L49 33 L46 36" fill="none" stroke="#FF7A3D" strokeWidth="1.1" strokeLinecap="round"/>
    <path d="M36 37 L46 39 M54 39 L64 37" stroke="#14100E" strokeWidth="2.4" strokeLinecap="round"/>
    <path d="M34 41 L66 41 L65 46 C64 50 56 50 54 46 L52 43 L48 43 L46 46 C44 50 36 50 35 46 Z" fill="#0E0B0A"/>
    <path d="M37 43 L41 43 M57 43 L61 43" stroke="#FFC14D" strokeWidth="1.2" strokeLinecap="round"/>
    <ellipse className="mouth" cx="50" cy="56.5" rx="3.6" ry="1.4" fill="#FF7A3D"/>
    </g>
  </svg>
);

export interface PortraitProps {
  /** One of the six gods, or Surtr, who shows up only on errors. */
  god: GodId | 'surtr';
  /** Mouth, head and blink animations run while true. */
  talking?: boolean;
  className?: string;
}

/** A round medallion portrait. Size it with the parent box (it fills 100% of it). */
export function Portrait({ god, talking = false, className }: PortraitProps) {
  const classes = ['portrait', talking ? 'talking' : '', className ?? ''].filter(Boolean).join(' ');
  return (
    <span className={classes} data-god={god}>
      {god === 'surtr' ? SURTR_SVG : SVGS[god]}
    </span>
  );
}

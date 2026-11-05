/* The Markdown the gods speak: headings, paragraphs, nested lists, bold, italics and inline code.
 * Small on purpose, so the components keep no dependency besides React. Text is never parsed as HTML. */
import type { ReactNode } from 'react';

interface List { ordered: boolean; start: number; items: Item[] }
interface Item { lines: string[]; lists: List[] }
type Block = { kind: 'heading'; text: string } | { kind: 'paragraph'; lines: string[] } | { kind: 'list'; list: List };

const LIST_ITEM = /^(\s*)([-*+]|\d+[.)])\s+(.*)$/;
const HEADING = /^#{1,6}\s+(.*?)\s*#*$/;

function parse(source: string): Block[] {
  const blocks: Block[] = [];
  let paragraph: string[] | null = null;
  let stack: { indent: number; list: List }[] = [];
  let blank = false;

  for (const line of source.split(/\r?\n/)) {
    if (!line.trim()) {
      paragraph = null;
      blank = true;
      continue;
    }
    const heading = HEADING.exec(line);
    const item = LIST_ITEM.exec(line);

    if (heading) {
      blocks.push({ kind: 'heading', text: heading[1] });
      paragraph = null;
      stack = [];
    } else if (item) {
      const indent = item[1].replace(/\t/g, '  ').length;
      const ordered = /\d/.test(item[2]);
      const entry: Item = { lines: [item[3]], lists: [] };
      while (stack.length > 1 && stack[stack.length - 1].indent > indent) stack.pop();
      const top = stack[stack.length - 1];
      if (!top) {
        const list = { ordered, start: ordered ? parseInt(item[2], 10) : 1, items: [entry] };
        blocks.push({ kind: 'list', list });
        stack = [{ indent, list }];
      } else if (indent > top.indent) {
        const list = { ordered, start: ordered ? parseInt(item[2], 10) : 1, items: [entry] };
        top.list.items[top.list.items.length - 1].lists.push(list);
        stack.push({ indent, list });
      } else {
        top.list.items.push(entry);
      }
      paragraph = null;
    } else if (stack.length && (!blank || /^\s/.test(line))) {
      // continuation of the last list item
      const top = stack[stack.length - 1].list;
      top.items[top.items.length - 1].lines.push(line.trim());
    } else {
      stack = [];
      if (paragraph) {
        paragraph.push(line);
      } else {
        paragraph = [line];
        blocks.push({ kind: 'paragraph', lines: paragraph });
      }
    }
    blank = false;
  }
  return blocks;
}

const INLINE = /(\*\*[^*]+\*\*|\*[^*\s][^*]*\*|`[^`]+`)/g;

function inline(text: string): ReactNode[] {
  return text.split(INLINE).map((part, i) => {
    if (i % 2 === 0) return part;
    if (part.startsWith('**')) return <strong key={i}>{part.slice(2, -2)}</strong>;
    if (part.startsWith('`')) return <code key={i}>{part.slice(1, -1)}</code>;
    return <em key={i}>{part.slice(1, -1)}</em>;
  });
}

function lines(texts: string[], tail?: ReactNode): ReactNode[] {
  const out = texts.flatMap((text, i) => (i === 0 ? inline(text) : [<br key={`br${i}`} />, ...inline(text)]));
  return tail ? [...out, <span key="tail">{tail}</span>] : out;
}

function renderList(list: List, key: number, tail?: ReactNode): ReactNode {
  const items = list.items.map((item, i) => {
    const last = i === list.items.length - 1;
    const tailHere = last && item.lists.length === 0 ? tail : undefined;
    return (
      <li key={i}>
        {lines(item.lines, tailHere)}
        {item.lists.map((sub, j) => renderList(sub, j, last && j === item.lists.length - 1 ? tail : undefined))}
      </li>
    );
  });
  return list.ordered
    ? <ol key={key} start={list.start === 1 ? undefined : list.start}>{items}</ol>
    : <ul key={key}>{items}</ul>;
}

/** Renders `text` as Markdown. `tail` (a typing caret) is placed at the end of the last line. */
export function Markdown({ text, tail }: { text: string; tail?: ReactNode }) {
  const blocks = parse(text);
  if (blocks.length === 0) return <>{tail}</>;
  return (
    <>
      {blocks.map((block, i) => {
        const end = i === blocks.length - 1 ? tail : undefined;
        if (block.kind === 'heading') return <h3 key={i}>{lines([block.text], end)}</h3>;
        if (block.kind === 'paragraph') return <p key={i}>{lines(block.lines, end)}</p>;
        return renderList(block.list, i, end);
      })}
    </>
  );
}

import { createServer, type IncomingMessage, type ServerResponse } from 'node:http';
import { readFile } from 'node:fs/promises';
import { join, normalize } from 'node:path';
import { fileURLToPath } from 'node:url';
import { Mistral } from '@mistralai/mistralai';
import type { ChatCompletionRequestMessage } from '@mistralai/mistralai/models/components';

const PORT = Number(process.env.PORT ?? 3000);
const MODEL = 'mistral-small-latest';
const PUBLIC_DIR = fileURLToPath(new URL('../public', import.meta.url));

const apiKey = process.env.MISTRAL_API_KEY;
if (!apiKey) {
  console.error('MISTRAL_API_KEY is not set.');
  console.error('Copy .env.example to .env and add your key, or export MISTRAL_API_KEY.');
  process.exit(1);
}

const client = new Mistral({ apiKey });

type ClientChatMessage = { role: 'system' | 'user' | 'assistant'; content: string };

function isClientChatMessage(value: unknown): value is ClientChatMessage {
  if (typeof value !== 'object' || value === null) return false;
  const m = value as Record<string, unknown>;
  return (
    (m.role === 'system' || m.role === 'user' || m.role === 'assistant') &&
    typeof m.content === 'string'
  );
}

function sendJson(res: ServerResponse, status: number, data: unknown): void {
  res.writeHead(status, { 'content-type': 'application/json; charset=utf-8' });
  res.end(JSON.stringify(data));
}

async function readJsonBody(req: IncomingMessage): Promise<unknown> {
  const chunks: Buffer[] = [];
  let size = 0;
  for await (const chunk of req) {
    const buf = chunk as Buffer;
    size += buf.length;
    if (size > 1_000_000) throw new Error('Payload too large');
    chunks.push(buf);
  }
  return JSON.parse(Buffer.concat(chunks).toString('utf8'));
}

function extractText(content: unknown): string {
  if (typeof content === 'string') return content;
  if (Array.isArray(content)) {
    return content
      .map((part) =>
        typeof part === 'object' && part !== null && 'text' in part
          ? String((part as { text: unknown }).text)
          : '',
      )
      .join('');
  }
  return '';
}

const server = createServer(async (req, res) => {
  const url = new URL(req.url ?? '/', `http://${req.headers.host ?? 'localhost'}`);

  if (req.method === 'POST' && url.pathname === '/api/chat') {
    let messages: unknown;
    try {
      messages = (await readJsonBody(req) as { messages?: unknown })?.messages;
    } catch {
      return sendJson(res, 400, { error: 'Request body must be valid JSON.' });
    }
    if (!Array.isArray(messages) || messages.length === 0 || !messages.every(isClientChatMessage)) {
      return sendJson(res, 400, {
        error: 'Body must be { "messages": [{ "role": "system|user|assistant", "content": "..." }] } with at least one message.',
      });
    }
    try {
      const completion = await client.chat.complete({
        model: MODEL,
        messages: messages as unknown as ChatCompletionRequestMessage[],
      });
      const reply = extractText(completion.choices?.[0]?.message?.content);
      return sendJson(res, 200, { reply });
    } catch (error) {
      console.error('Mistral API request failed:', error);
      return sendJson(res, 502, { error: 'The Mistral API request failed. Check the server logs.' });
    }
  }

  if (req.method === 'GET') {
    const pathname = url.pathname === '/' ? '/index.html' : url.pathname;
    const filePath = normalize(join(PUBLIC_DIR, pathname));
    if (!filePath.startsWith(PUBLIC_DIR)) return sendJson(res, 403, { error: 'Forbidden' });
    try {
      const body = await readFile(filePath);
      const contentType = filePath.endsWith('.html') ? 'text/html; charset=utf-8' : 'application/octet-stream';
      res.writeHead(200, { 'content-type': contentType });
      return res.end(body);
    } catch {
      return sendJson(res, 404, { error: 'Not found' });
    }
  }

  return sendJson(res, 404, { error: 'Not found' });
});

server.listen(PORT, () => {
  console.log(`mistral-chat listening on http://localhost:${PORT} (model: ${MODEL})`);
});

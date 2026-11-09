# mistral-chat

Minimal chat web app that calls the Mistral API with the official TypeScript SDK
(`@mistralai/mistralai`) using the `mistral-small-latest` model.

The `MISTRAL_API_KEY` stays on the server: the browser only talks to the local
`POST /api/chat` endpoint, which forwards the conversation to Mistral and
returns the reply.

## Prerequisites

- Node.js >= 20.6 (developed on Node 22)
- A Mistral API key from <https://console.mistral.ai/>

## Install and run locally

```bash
cd mistral-chat
npm install
cp .env.example .env   # then edit .env and set MISTRAL_API_KEY=your-key
npm run dev
```

Open <http://localhost:3000> in your browser.

## Commands

| Command | Description |
| --- | --- |
| `npm install` | Install dependencies |
| `npm run dev` | Start the server with auto-reload (loads `.env`) |
| `npm start` | Start the server without auto-reload (loads `.env`) |
| `npm run typecheck` | Type-check the server code with `tsc --noEmit` |

Set `PORT` in `.env` to change the port (default: `3000`).

## How it works

- `src/server.ts` — dependency-free Node HTTP server. Serves `public/index.html`
  and exposes `POST /api/chat`, which validates `{ messages: [...] }` and calls
  `client.chat.complete({ model: 'mistral-small-latest', messages })` from the
  official SDK. The API key is read from `process.env.MISTRAL_API_KEY` and is
  never sent to the browser.
- `public/index.html` — single-page chat UI (vanilla JS). It keeps the
  conversation history in the browser and posts it to `/api/chat` on every send.
- `.env` — holds `MISTRAL_API_KEY` locally and is git-ignored; only
  `.env.example` is committed.

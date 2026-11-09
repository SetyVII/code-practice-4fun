# mistral-chat-studio

The minimal Mistral chat app, migrated to a **Mistral Studio App** so Mistral builds
and hosts it. Same behavior as the local `../mistral-chat` version:
`mistral-small-latest` via the official SDK, API key only on the server.

Generated with the official Mistral CLI (`mistral init --caps core,fastapi,tanstack-start,apps`),
then customized:

- `apps/api/src/api/routers/api/internal/chat.py` — `POST /api/chat`, calls
  `client.chat.complete(model="mistral-small-latest", ...)` with the Python SDK
  (`mistralai`), using the app credential from `utils.mistral.mistral_client()`
  (`MISTRAL_API_KEY` secret, never the browser).
- `apps/web/src/routes/index.tsx` — single-page chat UI (TanStack Start) that posts
  the conversation to `/api/chat` and renders the reply.

## Prerequisites

- Git, [uv](https://docs.astral.sh/uv/), Docker (only if you add the `postgres` capability; not needed here), Bun, and a GitHub account
- A Mistral account whose plan includes **Apps** (Free / Pro / Team without pay-as-you-go cannot create apps), and your Mistral GitHub app connected
- Your `MISTRAL_API_KEY` from <https://console.mistral.ai/>

## Run locally

```bash
curl -fsSL https://mistral.ai/cli/install.sh | bash   # installs the mistral CLI (+ bun)
export PATH="$HOME/.mistral/bin:$PATH"
mistral login                                           # signs in via your browser
cd mistral-chat-studio
# set your key for local dev (never committed; .env is gitignored):
grep -q '^MISTRAL_API_KEY=' .env && sed -i 's/^MISTRAL_API_KEY=.*/MISTRAL_API_KEY=your-key/' .env
mistral dev                                             # prints http://<app>.apps.localhost:8080
```

## Deploy (Mistral hosts it)

```bash
cd mistral-chat-studio
# create an EMPTY repository in your GitHub account, then:
git remote add origin https://github.com/YOUR-ORG/mistral-chat-studio.git
git add -A && git commit -m "mistral chat app" && git push -u origin HEAD
mistral deploy                                          # builds the pushed commit, prints the app URL
```

Notes from the docs:

- Commit and push every change before `mistral deploy`; the working tree must be clean.
- The CLI never uploads `.env`: set `MISTRAL_API_KEY` in the **Secrets** tab of the app
  in Studio (per module that needs it — here: `api`).
- The first deploy writes the target to `mistral.json`; commit and push it.
- `mistral apps logs api` reads runtime logs; `mistral apps publish` makes the app
  available inside Vibe; `mistral apps delete` removes it (irreversible).
- Alternative without GitHub: `mistral deploy --from-disk` (experimental).

## Layout

- `mistral.json` — app modules: `api` (FastAPI, route `/api`, port 3000) and `web`
  (TanStack Start, route `/`, port 3001)
- `apps/api` — FastAPI backend; routes are discovered from `src/api/routers/`
- `apps/web` — TanStack Start frontend (`bun run dev` / `bun run build`)
- `deploy/docker/` — Dockerfiles the platform builds (`Dockerfile.api`, `Dockerfile.web`)
- `packages/py/` — shared Python workspace (`env`, `utils`, `cli`)

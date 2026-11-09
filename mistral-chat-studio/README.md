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
# `.env` is gitignored, so a fresh clone does not have one — create it for local dev:
printf 'MISTRAL_API_KEY=your-key\n' > .env
mistral dev                                             # prints http://<app>.apps.localhost:8080
```

Note: `mistral-chat/` (the sibling folder) is the *local-only* Node version — it has no
`mistral.json`, so `mistral deploy` rejects it. Only this folder is a Studio app.

## Deploy (Mistral hosts it)

The app lives inside this repository, so `origin` already points at it. From a clean,
pushed `main`:

```bash
cd ~/code-practice-4fun && git checkout main && git pull
cd mistral-chat-studio
mistral login
mistral deploy        # builds the pushed commit of origin/main, prints the app URL
```

If you prefer a dedicated repo instead, create an empty one in your GitHub account and
point `origin` at it (`git remote set-url origin https://github.com/YOUR-ORG/mistral-chat-studio.git`),
then commit and push from this folder.

Notes from the docs:

- `mistral deploy` builds the **pushed** commit: commit and push every change first, and the
  working tree (untracked files included) must be clean.
- Your Mistral account must be connected to GitHub and the Mistral GitHub app must see the
  repository; the CLI stops and offers the GitHub setup page if not.
- Your organization's plan must include Apps.
- The CLI never uploads `.env`: after the first deploy creates the app, set
  `MISTRAL_API_KEY` in Studio → the app → **Secrets** tab, on the `api` module.
- The first deploy writes the target into `mistral.json`; commit and push it.
- Alternative without GitHub: `mistral deploy --from-disk` (experimental).
- `mistral apps logs api` reads runtime logs; `mistral apps publish` makes the app
  available inside Vibe; `mistral apps delete` removes it (irreversible).

## Layout

- `mistral.json` — app modules: `api` (FastAPI, route `/api`, port 3000) and `web`
  (TanStack Start, route `/`, port 3001)
- `apps/api` — FastAPI backend; routes are discovered from `src/api/routers/`
- `apps/web` — TanStack Start frontend (`bun run dev` / `bun run build`)
- `deploy/docker/` — Dockerfiles the platform builds (`Dockerfile.api`, `Dockerfile.web`)
- `packages/py/` — shared Python workspace (`env`, `utils`, `cli`)

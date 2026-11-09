import { useState } from "react";
import type { FormEvent } from "react";
import { createFileRoute } from "@tanstack/react-router";

type ChatMessage = { role: "user" | "assistant"; content: string };

export const Route = createFileRoute("/")({
  component: ChatPage,
});

function ChatPage() {
  const [history, setHistory] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function send(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const text = input.trim();
    if (!text || pending) return;
    setInput("");
    setPending(true);
    setError(null);
    const messages: ChatMessage[] = [...history, { role: "user", content: text }];
    setHistory(messages);
    try {
      const res = await fetch("/api/chat", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ messages }),
      });
      const data = (await res.json()) as { reply?: string; detail?: string };
      if (!res.ok) throw new Error(data.detail ?? "Request failed");
      setHistory([...messages, { role: "assistant", content: data.reply ?? "" }]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Request failed");
    } finally {
      setPending(false);
    }
  }

  return (
    <main className="mx-auto flex h-dvh w-full max-w-2xl flex-col gap-4 p-4">
      <h1 className="text-xl font-semibold">Mistral Chat — mistral-small-latest</h1>
      <div className="flex flex-1 flex-col gap-2 overflow-y-auto rounded-lg border border-black/10 p-3 dark:border-white/10">
        {history.length === 0 && (
          <p className="text-sm text-black/50 dark:text-white/50">Ask something…</p>
        )}
        {history.map((message, index) => (
          <div
            key={index}
            className={
              message.role === "user"
                ? "max-w-[80%] self-end rounded-2xl bg-indigo-600 px-3 py-2 text-white"
                : "max-w-[80%] self-start rounded-2xl bg-black/5 px-3 py-2 dark:bg-white/10"
            }
          >
            <p className="whitespace-pre-wrap">{message.content}</p>
          </div>
        ))}
        {error && <p className="self-center text-sm text-red-600">{error}</p>}
      </div>
      <form onSubmit={send} className="flex gap-2">
        <input
          value={input}
          onChange={(event) => setInput(event.target.value)}
          placeholder="Type a message…"
          autoComplete="off"
          className="flex-1 rounded-lg border border-black/10 bg-transparent px-3 py-2 dark:border-white/10"
        />
        <button
          type="submit"
          disabled={pending}
          className="rounded-lg bg-indigo-600 px-4 py-2 text-white disabled:opacity-50"
        >
          Send
        </button>
      </form>
    </main>
  );
}

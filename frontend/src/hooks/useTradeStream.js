// TICKET-ADV116 — useTradeStream() with Server-Sent Events (SSE)

import { useEffect, useState } from "react";
import { api } from "@services/apiService.js";

const MAX_BUFFER = 200;

export function useTradeStream(url = "/api/v1/trades/stream") {
  const [trades, setTrades] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    let isCurrent = true;

    const upsertTrade = (trade) => {
      if (!trade?.id) return;

      setTrades((previous) => {
        const existingIndex = previous.findIndex((item) => item.id === trade.id);

        if (existingIndex === -1) {
          return [trade, ...previous].slice(0, MAX_BUFFER);
        }

        const next = [...previous];
        next[existingIndex] = { ...next[existingIndex], ...trade };
        return next;
      });
    };

    async function loadTrades() {
      try {
        const result = await api.listTrades({ page: 0, size: MAX_BUFFER });
        if (!isCurrent) return;

        setTrades((previous) => {
          const byId = new Map(previous.map((trade) => [trade.id, trade]));
          for (const trade of result?.items ?? []) {
            byId.set(trade.id, { ...byId.get(trade.id), ...trade });
          }
          return [...byId.values()].slice(0, MAX_BUFFER);
        });
      } catch {
        // The live feed can still populate the dashboard when the initial
        // request fails (for example while a session is being refreshed).
      }
    }

    loadTrades();
    const eventSource = new EventSource(url);

    eventSource.onopen = () => {
      setIsConnected(true);
    };

    const handleTrade = (event) => {
      try {
        upsertTrade(JSON.parse(event.data));
      } catch {
        // Ignore malformed JSON
      }
    };

    // Spring sends named `trade` events; onmessage only receives unnamed
    // events, so support both wire formats.
    eventSource.onmessage = handleTrade;
    eventSource.addEventListener("trade", handleTrade);

    // Optional named event support
    eventSource.addEventListener("trade-matched", (event) => {
      try {
        const updatedTrade = JSON.parse(event.data);

        upsertTrade(updatedTrade);
      } catch {
        // Ignore malformed JSON
      }
    });

    eventSource.onerror = () => {
      setIsConnected(false);
    };

    return () => {
      isCurrent = false;
      eventSource.close();
    };
  }, [url]);

  return {
    trades,
    isConnected,
  };
}

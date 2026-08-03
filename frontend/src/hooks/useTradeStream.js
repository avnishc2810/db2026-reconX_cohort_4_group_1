// TICKET-ADV116 — useTradeStream() with Server-Sent Events (SSE)

import { useEffect, useState } from "react";
import { api } from "@services/apiService.js";

const MAX_BUFFER = 200;

function upsertTrade(trades, updatedTrade) {
  const withoutExisting = trades.filter((trade) => trade.id !== updatedTrade.id);
  return [updatedTrade, ...withoutExisting].slice(0, MAX_BUFFER);
}

export function useTradeStream(url = "/api/v1/trades/stream") {
  const [trades, setTrades] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function loadExistingTrades() {
      try {
        const result = await api.listTrades({ size: MAX_BUFFER });

        if (!cancelled) {
          setTrades((currentTrades) =>
            result.items.reduce(
              (allTrades, trade) => upsertTrade(allTrades, trade),
              currentTrades
            )
          );
        }
      } catch {
        // The SSE connection can still provide new trades if the initial load fails.
      }
    }

    loadExistingTrades();

    const eventSource = new EventSource(url);

    eventSource.onopen = () => {
      setIsConnected(true);
    };

    eventSource.onmessage = (event) => {
      try {
        const trade = JSON.parse(event.data);

        setTrades((prev) => upsertTrade(prev, trade));
      } catch {
        // Ignore malformed JSON
      }
    };

    eventSource.addEventListener("trade", (event) => {
      try {
        const trade = JSON.parse(event.data);

        setTrades((prev) => upsertTrade(prev, trade));
      } catch {
        // Ignore malformed JSON
      }
    });

    // Optional named event support
    eventSource.addEventListener("trade-matched", (event) => {
      try {
        const updatedTrade = JSON.parse(event.data);

        setTrades((prev) =>
          prev.map((trade) =>
            trade.id === updatedTrade.id
              ? { ...trade, ...updatedTrade }
              : trade
          )
        );
      } catch {
        // Ignore malformed JSON
      }
    });

    eventSource.onerror = () => {
      setIsConnected(false);
    };

    return () => {
      cancelled = true;
      eventSource.close();
    };
  }, [url]);

  return {
    trades,
    isConnected,
  };
}

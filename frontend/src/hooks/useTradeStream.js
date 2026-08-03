// TICKET-ADV116 — useTradeStream() with Server-Sent Events (SSE)

import { useEffect, useState } from "react";

const MAX_BUFFER = 200;

export function useTradeStream(url = "/api/v1/trades/stream") {
  const [trades, setTrades] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const eventSource = new EventSource(url);

    eventSource.onopen = () => {
      setIsConnected(true);
    };

    eventSource.onmessage = (event) => {
      try {
        const trade = JSON.parse(event.data);

        setTrades((prev) =>
          [trade, ...prev].slice(0, MAX_BUFFER)
        );
      } catch {
        // Ignore malformed JSON
      }
    };

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
      eventSource.close();
    };
  }, [url]);

  return {
    trades,
    isConnected,
  };
}
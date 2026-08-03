// TICKET-ADV115 — useWebSocket(url) with auto-reconnect (exp backoff up to 5 tries).
import {
  useState,
  useEffect,
  useRef,
  useCallback,
} from "react";

const DEFAULTS = {
  reconnect: true,
  maxRetries: 5,
  baseDelay: 500,
  maxDelay: 30000,
};

export function useWebSocket(url, options = {}) {
  const {
    reconnect,
    maxRetries,
    baseDelay,
    maxDelay,
  } = {
    ...DEFAULTS,
    ...options,
  };

  const [data, setData] = useState(null);
  const [status, setStatus] = useState("connecting");

  const wsRef = useRef(null);
  const retriesRef = useRef(0);
  const timerRef = useRef(null);
  const shouldStopRef = useRef(false);

  const connect = useCallback(() => {
    setStatus("connecting");

    const ws = new WebSocket(url);
    wsRef.current = ws;

    ws.onopen = () => {
      retriesRef.current = 0;
      setStatus("open");
    };

    ws.onmessage = (event) => {
      try {
        setData(JSON.parse(event.data));
      } catch {
        setData(event.data);
      }
    };

    ws.onerror = () => {
      setStatus("error");
    };

    ws.onclose = () => {
      setStatus("closed");

      if (
        shouldStopRef.current ||
        !reconnect ||
        retriesRef.current >= maxRetries
      ) {
        return;
      }

      const delay = Math.min(
        maxDelay,
        baseDelay * (2 ** retriesRef.current)
      );

      retriesRef.current++;

      timerRef.current = setTimeout(connect, delay);
    };
  }, [url, reconnect, maxRetries, baseDelay, maxDelay]);

  useEffect(() => {
    shouldStopRef.current = false;

    connect();

    return () => {
      shouldStopRef.current = true;

      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }

      if (
        wsRef.current &&
        wsRef.current.readyState <= WebSocket.OPEN
      ) {
        wsRef.current.close();
      }
    };
  }, [connect]);

  const send = useCallback((payload) => {
    if (
      !wsRef.current ||
      wsRef.current.readyState !== WebSocket.OPEN
    ) {
      return;
    }

    wsRef.current.send(
      typeof payload === "string"
        ? payload
        : JSON.stringify(payload)
    );
  }, []);

  return {
    data,
    status,
    send,
  };
}
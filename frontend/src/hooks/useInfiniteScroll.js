// TICKET-ADV118 — useInfiniteScroll(loadMore)

import { useEffect, useRef } from "react";

export function useInfiniteScroll(
  loadMore,
  { rootMargin = "200px" } = {}
) {
  const sentinelRef = useRef(null);
  const loadMoreRef = useRef(loadMore);

  // Always keep the latest loadMore callback
  useEffect(() => {
    loadMoreRef.current = loadMore;
  }, [loadMore]);

  // Create the observer only once (unless rootMargin changes)
  useEffect(() => {
    const sentinel = sentinelRef.current;

    if (!sentinel) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreRef.current();
        }
      },
      {
        rootMargin,
        threshold: 0.1,
      }
    );

    observer.observe(sentinel);

    return () => {
      observer.disconnect();
    };
  }, [rootMargin]);

  return sentinelRef;
}
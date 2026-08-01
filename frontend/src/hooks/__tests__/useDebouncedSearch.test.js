import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { useDebouncedSearch } from '../useDebouncedSearch';

describe('useDebouncedSearch Hook (TICKET-ADV117)', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('returns initial query immediately', () => {
    const { result } = renderHook(() => useDebouncedSearch('initial', 300));
    expect(result.current).toBe('initial');
  });

  it('updates debounced query only after delay passes', () => {
    const { result, rerender } = renderHook(
      ({ query, delay }) => useDebouncedSearch(query, delay),
      { initialProps: { query: 'A', delay: 300 } }
    );

    expect(result.current).toBe('A');

    rerender({ query: 'AA', delay: 300 });
    expect(result.current).toBe('A');

    act(() => {
      vi.advanceTimersByTime(200);
    });
    expect(result.current).toBe('A');

    act(() => {
      vi.advanceTimersByTime(100);
    });
    expect(result.current).toBe('AA');
  });
});

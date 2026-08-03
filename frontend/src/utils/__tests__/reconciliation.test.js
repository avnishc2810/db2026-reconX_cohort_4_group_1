import { describe, expect, it } from 'vitest';
import { summariseReconciliation } from '../reconciliation.js';

const trade = (id, side, quantity, price, instrumentId = 7) => ({
  id,
  side,
  quantity,
  price,
  instrumentId,
});

describe('summariseReconciliation', () => {
  it('matches equal BUY and SELL notionals for one instrument', () => {
    const summary = summariseReconciliation([
      trade(1, 'BUY', '10', '25.50'),
      trade(2, 'SELL', '10', '25.50'),
    ]);

    expect(summary).toEqual({ matched: 2, openBreaks: 0 });
  });

  it('leaves an unmatched leg as an open break', () => {
    const summary = summariseReconciliation([
      trade(1, 'BUY', 10, 25),
      trade(2, 'SELL', 5, 25),
    ]);

    expect(summary).toEqual({ matched: 0, openBreaks: 2 });
  });

  it('does not match trades from different instruments', () => {
    const summary = summariseReconciliation([
      trade(1, 'BUY', 10, 25, 7),
      trade(2, 'SELL', 10, 25, 8),
    ]);

    expect(summary).toEqual({ matched: 0, openBreaks: 2 });
  });
});

const AMOUNT_DECIMALS = 8;

function matchingKey(trade) {
  const instrument = trade.instrumentId ?? trade.instrumentSymbol;
  const quantity = Number(trade.quantity);
  const price = Number(trade.price);

  if (instrument == null || !Number.isFinite(quantity) || !Number.isFinite(price)) {
    return null;
  }

  // The API serialises decimal values as JSON numbers. Normalising the
  // product prevents harmless binary floating-point differences from turning
  // equal notionals into breaks.
  return `${instrument}:${(quantity * price).toFixed(AMOUNT_DECIMALS)}`;
}

/**
 * Pair one BUY and one SELL for the same instrument and notional. A trade may
 * only be used once, so two buys and one sell produce one pair and one break.
 */
export function summariseReconciliation(trades) {
  const unmatchedBuys = new Map();
  const unmatchedSells = new Map();
  let matched = 0;
  let openBreaks = 0;

  for (const trade of trades) {
    const key = matchingKey(trade);
    const side = String(trade.side ?? '').toUpperCase();

    if (!key || (side !== 'BUY' && side !== 'SELL')) {
      openBreaks += 1;
      continue;
    }

    const ownSide = side === 'BUY' ? unmatchedBuys : unmatchedSells;
    const oppositeSide = side === 'BUY' ? unmatchedSells : unmatchedBuys;
    const oppositeCount = oppositeSide.get(key) ?? 0;

    if (oppositeCount > 0) {
      matched += 2;
      oppositeSide.set(key, oppositeCount - 1);
    } else {
      ownSide.set(key, (ownSide.get(key) ?? 0) + 1);
    }
  }

  for (const count of unmatchedBuys.values()) openBreaks += count;
  for (const count of unmatchedSells.values()) openBreaks += count;

  return { matched, openBreaks };
}

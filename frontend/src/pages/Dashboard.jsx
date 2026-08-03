// TICKET-ADV120 — useMemo for portfolio-value calc.
// TICKET-ADV116 — useTradeStream live feed.

import React, { useMemo } from 'react';
import { withAuth } from '@components/withAuth.jsx';
import { useTradeStream } from '@hooks/useTradeStream.js';
import { summariseReconciliation } from '@/utils/reconciliation.js';

function StatCard({ label, value }) {
  return (
    <article className="stat-card">
      <h3>{label}</h3>
      <p>{value}</p>
    </article>
  );
}

function Dashboard() {
  const { trades, isConnected } = useTradeStream();

  // Portfolio value
  const portfolioValue = useMemo(
    () =>
      trades.reduce(
        (sum, trade) =>
          sum + ((trade.quantity || 0) * (trade.price || 0)),
        0
      ),
    [trades]
  );

  // Trade statistics
  const { matched, openBreaks } = useMemo(
    () => summariseReconciliation(trades),
    [trades]
  );

  return (
    <section>
      <h2>Dashboard</h2>

      <div className="stat-grid">
        <StatCard
          label="Portfolio value (USD)"
          value={portfolioValue.toLocaleString()}
        />

        <StatCard
          label="Trades streamed"
          value={trades.length}
        />

        <StatCard
          label="Matched"
          value={matched}
        />

        <StatCard
          label="Open breaks"
          value={openBreaks}
        />
      </div>

      <div role="status" aria-live="polite">
        SSE: {isConnected ? 'connected' : 'disconnected'}
      </div>
    </section>
  );
}

export default withAuth(Dashboard);

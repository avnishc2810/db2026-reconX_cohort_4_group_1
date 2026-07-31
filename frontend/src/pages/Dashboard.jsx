// TICKET-ADV120 — useMemo for portfolio-value calc.
// TICKET-ADV116 — useTradeStream live feed.
import React from 'react';
import { withAuth } from '@components/withAuth.jsx';
import { useTradeStream } from '@hooks/useTradeStream.js';

function StatCard({ label, value }) {
  return (
    <article className="stat-card">
      <h3>{label}</h3>
      <p>{value}</p>
    </article>
  );
}

function Dashboard({ trades: tradesProp }) {
  const streamData = useTradeStream();
  const trades = tradesProp || streamData.trades || [];
  const isConnected = streamData.isConnected;

  const portfolioValue = React.useMemo(() => {
    return trades.reduce((sum, t) => sum + (t.quantity * t.price), 0);
  }, [trades]);

  const matchedCount = React.useMemo(() => {
    return trades.filter(t => t.status === 'MATCHED').length;
  }, [trades]);

  const unmatchedCount = React.useMemo(() => {
    return trades.filter(t => t.status === 'UNMATCHED' || t.status === 'DISPUTED').length;
  }, [trades]);

  return (
    <section>
      <h2>Dashboard</h2>
      <div className="stat-grid">
        <StatCard label="Portfolio value" value={`$${portfolioValue.toLocaleString()}`} />
        <StatCard label="Matched trades" value={matchedCount} />
        <StatCard label="Unmatched trades" value={unmatchedCount} />
      </div>
      <div role="status" aria-live="polite">
        SSE: {isConnected ? 'connected' : 'disconnected'}
      </div>
    </section>
  );
}

export default withAuth(Dashboard);

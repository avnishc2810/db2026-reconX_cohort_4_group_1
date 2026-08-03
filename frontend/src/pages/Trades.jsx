// TICKET-ADV114 — Compound DataTable.
// TICKET-ADV117 — useDebouncedSearch.
// TICKET-ADV121 — useCallback on memoised TradeRow.

import React, { useState, useEffect, useCallback } from 'react';
import { withAuth } from '@components/withAuth.jsx';
import DataTable from '@components/DataTable.jsx';
import { TradeRow } from '@components/TradeRow.jsx';
import { useDebouncedSearch } from '@hooks/useDebouncedSearch.js';
import { api } from '@services/apiService.js';

function Trades() {
  const [search, setSearch] = useState('');
  const debounced = useDebouncedSearch(search, 300);

  const [page, setPage] = useState(0);
  const [data, setData] = useState({
    items: [],
    totalPages: 0,
  });

  // ADV121
  const [selectedId, setSelectedId] = useState(null);

  const handleSelect = useCallback((id) => {
    setSelectedId(id);
  }, []);

  // ADV114 + ADV117
  useEffect(() => {
    async function loadTrades() {
      try {
        const params = { page };

        if (debounced) {
          params.status = debounced;
        }

        const result = await api.listTrades(params);
        setData(result);
      } catch (err) {
        console.error(err);
        setData({
          items: [],
          totalPages: 0,
        });
      }
    }

    loadTrades();
  }, [page, debounced]);

  return (
    <section>
      <h2>Trades</h2>

      <input
        aria-label="Filter by status"
        placeholder="status filter (PENDING/MATCHED/...)"
        value={search}
        onChange={(e) => setSearch(e.target.value.toUpperCase())}
      />

      <DataTable>
        <DataTable.Header
          columns={[
            { key: 'tradeRef', label: 'Ref' },
            { key: 'symbol', label: 'Symbol' },
            { key: 'qty', label: 'Qty' },
            { key: 'price', label: 'Price' },
            { key: 'status', label: 'Status' },
          ]}
        />

        <DataTable.Body
          rows={data.items}
          render={(trade) => (
            <TradeRow
              key={trade.id}
              trade={trade}
              onClick={handleSelect}
            />
          )}
        />

        <DataTable.Pagination
          page={page}
          totalPages={Math.max(1, data.totalPages)}
          onChange={setPage}
        />
      </DataTable>
    </section>
  );
}

export default withAuth(Trades);
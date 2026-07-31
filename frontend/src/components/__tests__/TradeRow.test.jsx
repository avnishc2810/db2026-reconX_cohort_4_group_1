import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import React from 'react';
import { TradeRow } from '../TradeRow';

describe('TradeRow Component (TICKET-ADV119)', () => {
  const sampleTrade = {
    id: 1,
    tradeRef: 'TR-1001',
    instrument: 'SAP.DE',
    quantity: 100,
    price: 245.5,
    status: 'PENDING',
  };

  it('renders trade details correctly inside table row', () => {
    render(
      <table>
        <tbody>
          <TradeRow trade={sampleTrade} onClick={() => {}} />
        </tbody>
      </table>
    );

    expect(screen.getByText('TR-1001')).toBeInTheDocument();
    expect(screen.getByText('SAP.DE')).toBeInTheDocument();
    expect(screen.getByText('100')).toBeInTheDocument();
    expect(screen.getByText('245.5')).toBeInTheDocument();
    expect(screen.getByText('PENDING')).toBeInTheDocument();
  });

  it('triggers onClick handler with trade id when row is clicked', () => {
    const handleClick = vi.fn();
    render(
      <table>
        <tbody>
          <TradeRow trade={sampleTrade} onClick={handleClick} />
        </tbody>
      </table>
    );

    fireEvent.click(screen.getByText('TR-1001'));
    expect(handleClick).toHaveBeenCalledWith(1);
  });
});

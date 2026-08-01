import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import React from 'react';
import AddTrade from '../AddTrade';
import { AuthContext } from '@context/AuthContext';
import { MemoryRouter } from 'react-router-dom';

function renderWithAuth(ui) {
  const user = { email: 'trader@db.com', role: 'TRADER' };
  return render(
    <AuthContext.Provider value={{ user, isLoading: false }}>
      <MemoryRouter>{ui}</MemoryRouter>
    </AuthContext.Provider>
  );
}

describe('<AddTrade /> Page (TICKET-ADV123)', () => {
  it('renders form heading and submit button', () => {
    renderWithAuth(<AddTrade />);
    expect(screen.getByRole('heading', { name: /add trade/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
  });

  it('shows error alerts when submitting empty form', async () => {
    renderWithAuth(<AddTrade />);
    fireEvent.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => {
      expect(screen.getAllByRole('alert').length).toBeGreaterThan(0);
    });
  });
});

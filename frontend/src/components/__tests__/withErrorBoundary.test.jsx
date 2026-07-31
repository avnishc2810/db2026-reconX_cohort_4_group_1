import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import React from 'react';
import { withErrorBoundary } from '../withErrorBoundary';

const ProblemChild = ({ shouldThrow }) => {
  if (shouldThrow) {
    throw new Error('Test render crash');
  }
  return <div>Healthy Child</div>;
};

const WrappedComponent = withErrorBoundary(ProblemChild);

describe('withErrorBoundary HOC (TICKET-ADV113)', () => {
  it('renders children when no error occurs', () => {
    render(<WrappedComponent shouldThrow={false} />);
    expect(screen.getByText('Healthy Child')).toBeInTheDocument();
  });

  it('catches render error, displays role="alert" fallback, and resets on "Try again" click', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const { rerender } = render(<WrappedComponent shouldThrow={true} />);

    expect(screen.getByRole('alert')).toBeInTheDocument();
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    expect(screen.getByText(/Test render crash/)).toBeInTheDocument();

    const tryAgainBtn = screen.getByRole('button', { name: /try again/i });
    expect(tryAgainBtn).toBeInTheDocument();

    rerender(<WrappedComponent shouldThrow={false} />);
    fireEvent.click(tryAgainBtn);

    expect(screen.getByText('Healthy Child')).toBeInTheDocument();

    consoleSpy.mockRestore();
  });
});

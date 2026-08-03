package com.dbtraining.reconx.exception;

/**
 * Exception thrown when a requested reconciliation break cannot be found.
 */
public class ReconBreakNotFoundException extends RuntimeException {

    public ReconBreakNotFoundException(Long id) {
        super("Recon break not found: " + id);
    }

    public ReconBreakNotFoundException(String id) {
        super("Recon break not found: " + id);
    }
}
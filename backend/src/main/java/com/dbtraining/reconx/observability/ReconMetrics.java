package com.dbtraining.reconx.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ReconMetrics {

    private final Timer reconciliationTimer;

    public ReconMetrics(MeterRegistry registry) {
        this.reconciliationTimer = Timer.builder("reconciliation_duration_seconds")
                .description("Wall time of reconciliation engine per batch")
                .publishPercentileHistogram(true)   // REQUIRED for Prometheus histogram buckets
                .publishPercentiles(0.5, 0.95, 0.99) // Optional client-side quantiles
                .register(registry);
    }

    public Timer reconciliationTimer() {
        return reconciliationTimer;
    }
}

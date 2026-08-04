package com.dbtraining.reconx.observability;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dbtraining.reconx.repository.TradeRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * TICKET-ADV092 — current trade counts split by lifecycle status.
 *
 * Gauges read from the repository at scrape time, so they never become stale
 * when a trade is reconciled, updated, or cancelled.
 */
@Component
public class TradesByStatusGauge {

    private static final List<String> STATUSES = List.of(
            "PENDING", "MATCHED", "UNMATCHED", "DISPUTED", "CANCELLED");

    public TradesByStatusGauge(MeterRegistry registry, TradeRepository tradeRepository) {
        STATUSES.forEach(status -> Gauge.builder(
                        "trades_by_status", tradeRepository,
                        repository -> repository.countByStatus(status))
                .description("Current trades by status")
                .tag("status", status)
                .register(registry));
    }
}

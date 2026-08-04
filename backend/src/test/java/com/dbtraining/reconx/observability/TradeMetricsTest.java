package com.dbtraining.reconx.observability;

import com.dbtraining.reconx.repository.ReconBreakRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV083 — Unit test for TradeMetrics trade_created_total counter.
 */
class TradeMetricsTest {

    private MeterRegistry registry;
    private ReconBreakRepository breakRepo;
    private TradeMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        breakRepo = (ReconBreakRepository) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{ReconBreakRepository.class},
                (proxy, method, args) -> method.getName().equals("countByStatus") ? 0L : null);
        metrics = new TradeMetrics(registry, breakRepo);
    }

    @Test
    void testIncrementTradeCreated_incrementsCounter() {
        Counter counter = registry.find("trade_created_total").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(0.0);

        metrics.incrementTradeCreated();
        metrics.incrementTradeCreated();

        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void recordTradeValue_recordsDistributionSummary() {
        DistributionSummary summary = registry.find("trade_value_total").summary();
        assertThat(summary).isNotNull();

        metrics.recordTradeValue(100.25);

        assertThat(summary.count()).isEqualTo(1);
        assertThat(summary.totalAmount()).isEqualTo(100.25);
    }
}

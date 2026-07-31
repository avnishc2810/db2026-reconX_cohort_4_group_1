package com.dbtraining.reconx.observability;

import com.dbtraining.reconx.repository.ReconBreakRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        breakRepo = Mockito.mock(ReconBreakRepository.class);
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
}

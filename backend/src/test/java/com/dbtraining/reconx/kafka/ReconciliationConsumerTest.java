package com.dbtraining.reconx.kafka;

import com.dbtraining.reconx.dto.TradeEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * TICKET-ADV131 — Unit test for ReconciliationConsumer.
 */
class ReconciliationConsumerTest {

    @Test
    void testOnTradeEvent_logsEventWithoutException() {
        ReconciliationConsumer consumer = new ReconciliationConsumer();
        TradeEvent event = new TradeEvent(
                UUID.randomUUID().toString(),
                "TRD-20260315-1111",
                "TRADE_CREATED",
                100L,
                "SAP.DE",
                Instant.now()
        );

        assertThatCode(() -> consumer.onTradeEvent(event)).doesNotThrowAnyException();
    }
}

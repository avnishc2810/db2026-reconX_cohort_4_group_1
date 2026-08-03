package com.dbtraining.reconx.kafka;

import com.dbtraining.reconx.dto.TradeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;

/**
 * TICKET-ADV129 — Unit test for TradeEventProducer.
 */
class TradeEventProducerTest {

    private KafkaTemplate<String, TradeEvent> template;
    private TradeEventProducer producer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        template = Mockito.mock(KafkaTemplate.class);
        producer = new TradeEventProducer(template);
    }

    @Test
    void testPublish_delegatesToKafkaTemplate() {
        TradeEvent event = new TradeEvent(
                UUID.randomUUID().toString(),
                "TRD-20260315-9999",
                "TRADE_CREATED",
                1L,
                "SAP.DE",
                Instant.now()
        );

        producer.publish(event);

        verify(template).send("trade-events", "TRD-20260315-9999", event);
    }
}

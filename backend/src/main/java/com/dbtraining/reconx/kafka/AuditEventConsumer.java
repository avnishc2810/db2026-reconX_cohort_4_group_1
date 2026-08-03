package com.dbtraining.reconx.kafka;

import com.dbtraining.reconx.dto.TradeEvent;
import com.dbtraining.reconx.repository.AuditLogRepository;
import com.dbtraining.reconx.repository.entity.AuditLogEntry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(AuditEventConsumer.class);

    private final AuditLogRepository repo;

    public AuditEventConsumer(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Transactional
    @KafkaListener(
            topics = "trade-events",
            groupId = "audit-service"
    )
    public void onTradeEvent(TradeEvent e) {

        AuditLogEntry entry = new AuditLogEntry(
                e.eventId().toString(),
                e.tradeRef(),
                e.eventType().name(),
                e.timestamp(),
                e.actor(),
                e.before(),
                e.after()
        );

        repo.save(entry);

        log.debug(
                "Audit row persisted for eventId={}, tradeRef={}",
                e.eventId(),
                e.tradeRef()
        );
    }
}
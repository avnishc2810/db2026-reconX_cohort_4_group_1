package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeEvent;
import com.dbtraining.reconx.entity.AuditLogEntry;
import com.dbtraining.reconx.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditQueryService {

    private final AuditLogRepository auditLogRepository;

    public AuditQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<TradeEvent> eventsForTrade(String tradeRef) {
        return auditLogRepository.findByTradeRefOrderByTimestampAsc(tradeRef)
                .stream()
                .map(this::toTradeEvent)
                .toList();
    }

    private TradeEvent toTradeEvent(AuditLogEntry entry) {
        return new TradeEvent(
                entry.getTradeRef(),
                entry.getEventType(),
                entry.getTimestamp(),
                entry.getPayload()
        );
    }
}
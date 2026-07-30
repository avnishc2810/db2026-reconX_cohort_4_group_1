package com.dbtraining.reconx.controller;

import com.dbtraining.reconx.repository.AuditLogRepository;
import com.dbtraining.reconx.repository.entity.AuditLogEntry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * TICKET-ADV071 — Unit test for AuditController history endpoint.
 */
class AuditControllerTest {

    @Test
    void testHistory_returnsAuditLogEntriesFromRepository() {
        AuditLogRepository auditRepo = mock(AuditLogRepository.class);
        AuditLogEntry entry = new AuditLogEntry();
        entry.setTradeRef("EQU-20260603-0001");
        entry.setEventType("TRADE_CREATED");
        entry.setEventTimestamp(Instant.now());

        when(auditRepo.findByTradeRefOrderByEventTimestampAsc("EQU-20260603-0001"))
                .thenReturn(List.of(entry));

        AuditController controller = new AuditController(auditRepo);
        List<AuditLogEntry> result = controller.history("EQU-20260603-0001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTradeRef()).isEqualTo("EQU-20260603-0001");
        verify(auditRepo).findByTradeRefOrderByEventTimestampAsc("EQU-20260603-0001");
    }
}

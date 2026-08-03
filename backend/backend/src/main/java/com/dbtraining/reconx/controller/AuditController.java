package com.dbtraining.reconx.controller;

import com.dbtraining.reconx.dto.TradeEvent;
import com.dbtraining.reconx.service.AuditQueryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit/trades")
@PreAuthorize("hasAnyRole('ADMIN','RECON_ANALYST')")
public class AuditController {

    private final AuditQueryService auditQueryService;

    public AuditController(AuditQueryService auditQueryService) {
        this.auditQueryService = auditQueryService;
    }

    @GetMapping("/{tradeRef}/events")
    public List<TradeEvent> getEvents(@PathVariable String tradeRef) {
        return auditQueryService.eventsForTrade(tradeRef);
    }
}
package com.dbtraining.reconx.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TradeResponse(
        Long id,
        String tradeRef,
        LocalDate tradeDate,
        Long instrumentId,
        Long counterpartyId,
        Integer quantity,
        Double price,
        String status,
        String updatedBy,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}

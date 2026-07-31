package com.dbtraining.reconx.dto;

import com.dbtraining.reconx.repository.entity.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public TradeResponse toResponse(Trade t) {
        return new TradeResponse(
                t.getId(),
                t.getTradeRef(),
                t.getTradeDate(),
                t.getInstrumentId(),
                t.getCounterpartyId(),
                t.getQuantity(),
                t.getPrice(),
                t.getStatus(),
                t.getUpdatedBy(),
                t.getUpdatedAt(),
                t.getDeletedAt()
        );
    }
}

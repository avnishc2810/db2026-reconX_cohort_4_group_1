package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeRequest;
import com.dbtraining.reconx.exception.TradeNotFoundException;
import com.dbtraining.reconx.kafka.TradeEventProducer;
import com.dbtraining.reconx.observability.TradeMetrics;
import com.dbtraining.reconx.repository.CounterpartyRepository;
import com.dbtraining.reconx.repository.InstrumentRepository;
import com.dbtraining.reconx.repository.TradeRepository;
import com.dbtraining.reconx.repository.entity.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * TICKET-ADV065 — Unit test for TradeService.update full trade update.
 */
class TradeServiceTest {

    private TradeRepository tradeRepo;
    private CounterpartyRepository cpRepo;
    private InstrumentRepository instRepo;
    private TradeEventProducer events;
    private TradeMetrics metrics;
    private TradeService service;

    @BeforeEach
    void setUp() {
        tradeRepo = mock(TradeRepository.class);
        cpRepo = mock(CounterpartyRepository.class);
        instRepo = mock(InstrumentRepository.class);
        events = mock(TradeEventProducer.class);
        metrics = mock(TradeMetrics.class);
        service = new TradeService(tradeRepo, cpRepo, instRepo, events, metrics);
    }

    @Test
    void testUpdate_whenTradeExists_updatesAndReturnsSavedTrade() {
        Trade existing = new Trade();
        existing.setTradeRef("EQU-20260603-0001");
        existing.setQuantity(new BigDecimal("100"));
        existing.setPrice(new BigDecimal("50.00"));

        when(tradeRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(tradeRepo.save(any(Trade.class))).thenAnswer(i -> i.getArgument(0));

        TradeRequest req = new TradeRequest(
                "EQU-20260603-0001",
                1L, 1L,
                "EQUITY", "BUY",
                new BigDecimal("200"),
                new BigDecimal("55.00"),
                LocalDate.of(2026, 6, 3)
        );

        Trade updated = service.update(1L, req, "trader1");

        assertThat(updated.getQuantity()).isEqualTo(new BigDecimal("200"));
        assertThat(updated.getPrice()).isEqualTo(new BigDecimal("55.00"));
        verify(tradeRepo).save(existing);
    }

    @Test
    void testUpdate_whenTradeNotFound_throwsTradeNotFoundException() {
        when(tradeRepo.findById(99L)).thenReturn(Optional.empty());

        TradeRequest req = new TradeRequest(
                "EQU-20260603-0001",
                1L, 1L,
                "EQUITY", "BUY",
                new BigDecimal("200"),
                new BigDecimal("55.00"),
                LocalDate.of(2026, 6, 3)
        );

        assertThatThrownBy(() -> service.update(99L, req, "trader1"))
                .isInstanceOf(TradeNotFoundException.class);
    }
}

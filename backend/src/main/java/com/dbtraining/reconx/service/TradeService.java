package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeRequest;
import com.dbtraining.reconx.exception.DuplicateTradeRefException;
import com.dbtraining.reconx.exception.TradeNotFoundException;
import com.dbtraining.reconx.kafka.TradeEventProducer;
import com.dbtraining.reconx.observability.TradeMetrics;
import com.dbtraining.reconx.repository.CounterpartyRepository;
import com.dbtraining.reconx.repository.InstrumentRepository;
import com.dbtraining.reconx.repository.TradeRepository;
import com.dbtraining.reconx.repository.entity.Trade;
import com.dbtraining.reconx.dto.TradeEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.dbtraining.reconx.repository.TradeSpecifications.*;

@Service
@Transactional
public class TradeService {

    private final TradeRepository tradeRepo;
    private final CounterpartyRepository cpRepo;
    private final InstrumentRepository instRepo;
    private final TradeEventProducer events;
    private final TradeMetrics metrics;

    public TradeService(TradeRepository tradeRepo,
                        CounterpartyRepository cpRepo,
                        InstrumentRepository instRepo,
                        TradeEventProducer events,
                        TradeMetrics metrics) {
        this.tradeRepo = tradeRepo;
        this.cpRepo = cpRepo;
        this.instRepo = instRepo;
        this.events = events;
        this.metrics = metrics;
    }

    public Trade create(TradeRequest req, String actor) {
        throw new UnsupportedOperationException("TICKET-ADV064");
    }

    public Trade update(Long id, TradeRequest req, String actor) {
        throw new UnsupportedOperationException("TICKET-ADV065");
    }

    public void softDelete(Long id, String actor) {

    var trade = tradeRepo.findById(id)
            .orElseThrow(() -> new TradeNotFoundException(id));

    trade.softDelete();                 // sets deletedAt = now
    trade.setUpdatedBy(actor);          // who performed the delete
    trade.setUpdatedAt(LocalDateTime.now());

    tradeRepo.save(trade);
}


    // ⭐⭐⭐ THIS IS THE ONLY VALID updateStatus METHOD ⭐⭐⭐
    public Trade updateStatus(Long id, String status, String actor) {

        var trade = tradeRepo.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));

        trade.setStatus(status);
        trade.setUpdatedBy(actor);
        trade.setUpdatedAt(LocalDateTime.now());

        return tradeRepo.save(trade);
    }

    public void softDelete(Long id, String actor) {
        throw new UnsupportedOperationException("TICKET-ADV067");
    }

    @Transactional(readOnly = true)
    public Page<Trade> list(LocalDate from, LocalDate to, String status, Long counterpartyId, Pageable pageable) {
        throw new UnsupportedOperationException("TICKET-ADV055");
    }
}

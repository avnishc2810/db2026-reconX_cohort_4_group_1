package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeRequest;
import com.dbtraining.reconx.dto.TradeMapper;
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

import java.time.Instant;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.dbtraining.reconx.repository.TradeSpecifications.*;

/**
 * ============================================================================
 * TICKET-ADV064 — TradeService.create (POST endpoint backing)
 * TICKET-ADV065 — update
 * TICKET-ADV066 — updateStatus (PATCH)
 * TICKET-ADV067 — softDelete
 * TICKET-ADV083 — increments trade_created_total Counter on create
 * TICKET-ADV129 — publishes TradeEvent on every state change
 * TICKET-ADV055/ADV056 — list() uses Specifications + filter query
 * ============================================================================
 */
@Service
@Transactional
public class TradeService {

    private final TradeRepository tradeRepo;
    private final CounterpartyRepository cpRepo;
    private final InstrumentRepository instRepo;
    private final TradeEventProducer events;
    private final TradeMetrics metrics;
    private final TradeMapper mapper;
    private final TradeStreamService tradeStreamService;

    public TradeService(TradeRepository tradeRepo,
                        CounterpartyRepository cpRepo,
                        InstrumentRepository instRepo,
                        TradeEventProducer events,
                        TradeMetrics metrics,
                        TradeMapper mapper,
                        TradeStreamService tradeStreamService) {
        this.tradeRepo = tradeRepo;
        this.cpRepo = cpRepo;
        this.instRepo = instRepo;
        this.events = events;
        this.metrics = metrics;
        this.mapper = mapper;
        this.tradeStreamService = tradeStreamService;
    }

        public Trade create(TradeRequest req, String actor) {
        
        if (tradeRepo.findByTradeRef(req.tradeRef()).isPresent()) {
            throw new DuplicateTradeRefException(req.tradeRef());
        }
        Trade trade = new Trade();

        trade.setTradeRef(req.tradeRef());

        trade.setInstrument(
                instRepo.findById(req.instrumentId())
                        .orElseThrow(() ->
                                new TradeNotFoundException(
                                        "Instrument not found"))
        );

        trade.setCounterparty(
                cpRepo.findById(req.counterpartyId())
                        .orElseThrow(() ->
                                new TradeNotFoundException(
                                        "Counterparty not found"))
        );

        trade.setAssetClass(req.assetClass());
        trade.setSide(req.side());
        trade.setQuantity(req.quantity());
        trade.setPrice(req.price());
        trade.setTradeDate(req.tradeDate());

        trade.setStatus("PENDING");

        Trade saved = tradeRepo.save(trade);
        Trade reconciled = reconcileNewTrade(saved);
        publish(reconciled);
        return reconciled;
    }

    public Trade update(Long id, TradeRequest req, String actor) {

        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() ->
                        new TradeNotFoundException(String.valueOf(id)));

        trade.setTradeRef(req.tradeRef());

        trade.setInstrument(
                instRepo.findById(req.instrumentId())
                        .orElseThrow(() ->
                                new TradeNotFoundException("Instrument not found"))
        );

        trade.setCounterparty(
                cpRepo.findById(req.counterpartyId())
                        .orElseThrow(() ->
                                new TradeNotFoundException("Counterparty not found"))
        );

        trade.setAssetClass(req.assetClass());
        trade.setSide(req.side());
        trade.setQuantity(req.quantity());
        trade.setPrice(req.price());
        trade.setTradeDate(req.tradeDate());

        Trade saved = tradeRepo.save(trade);
        publish(saved);
        return saved;
    }

    public Trade updateStatus(Long id, String status, String actor) {

        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() ->
                        new TradeNotFoundException(String.valueOf(id)));

        trade.setStatus(status);

        Trade saved = tradeRepo.save(trade);
        publish(saved);
        return saved;
    }

    public void softDelete(Long id, String actor) {

        Trade trade = tradeRepo.findById(id)
                .orElseThrow(() ->
                        new TradeNotFoundException("id=" + id));

        trade.softDelete();

        tradeRepo.save(trade);

        events.publish(
                new TradeEvent(
                        UUID.randomUUID(),
                        trade.getTradeRef(),
                        TradeEvent.EventType.TRADE_CANCELLED,
                        Instant.now(),
                        actor,
                        null,
                        null
                )
        );
    }

    /**
     * Matches the newly-created trade to the oldest available opposite-side
     * trade for the same instrument and exact notional (quantity × price).
     * Every trade can belong to only one pair.
     */
    private Trade reconcileNewTrade(Trade trade) {
        String oppositeSide = "BUY".equalsIgnoreCase(trade.getSide()) ? "SELL" : "BUY";
        List<Trade> candidates = tradeRepo
                .findByInstrument_IdAndSideAndStatusInOrderByCreatedAtAscIdAsc(
                        trade.getInstrument().getId(),
                        oppositeSide,
                        List.of("PENDING", "UNMATCHED"));

        Trade counterpart = candidates.stream()
                .filter(candidate -> sameNotional(trade, candidate))
                .findFirst()
                .orElse(null);

        if (counterpart == null) {
            trade.setStatus("UNMATCHED");
            return tradeRepo.save(trade);
        }

        trade.setStatus("MATCHED");
        counterpart.setStatus("MATCHED");
        tradeRepo.save(counterpart);
        publish(counterpart);
        return tradeRepo.save(trade);
    }

    private boolean sameNotional(Trade first, Trade second) {
        BigDecimal firstNotional = first.getQuantity().multiply(first.getPrice());
        BigDecimal secondNotional = second.getQuantity().multiply(second.getPrice());
        return firstNotional.compareTo(secondNotional) == 0;
    }

    private void publish(Trade trade) {
        tradeStreamService.broadcast(mapper.toResponse(trade));
    }

    @Transactional(readOnly = true)
    public Page<Trade> list(LocalDate from, LocalDate to, String status, Long counterpartyId, Pageable pageable) {
        Specification<Trade> spec = Specification.where(hasStatus(status))
                .and(tradeDateBetween(from, to))
                .and(hasCounterparty(counterpartyId));
        return tradeRepo.findAll(spec, pageable);
    }
}

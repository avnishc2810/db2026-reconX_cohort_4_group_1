package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import com.dbtraining.reconx.observability.ReconConfigMBean;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReconciliationEngine {

    private final ReconConfigMBean reconConfig;

    public ReconciliationEngine(ReconConfigMBean reconConfig) {
        this.reconConfig = reconConfig;
    }

    @Timed(
        value = "reconciliation_duration_seconds",
        description = "Wall time of reconcile()",
        percentiles = {0.5, 0.95, 0.99},
        histogram = true
    )
    public List<ReconResult> reconcile(List<TradeType> internal,
                                       List<TradeType> external,
                                       ReconciliationRule rule) {

        if (internal == null || internal.isEmpty()) {
            return List.of();
        }

        Map<String, TradeType> externalByRef =
                external == null ? Map.of() :
                external.stream().collect(
                        Collectors.toMap(
                                t -> t.tradeRef().value(),
                                Function.identity(),
                                (a, b) -> a
                        )
                );

        return internal.parallelStream()
                .map(in -> matchOne(in, externalByRef.get(in.tradeRef().value()), rule))
                .toList();
    }

    public CompletableFuture<List<ReconResult>> reconcileByCounterparty(
            Map<Long, List<TradeType>> internalByCp,
            Map<Long, List<TradeType>> externalByCp,
            ReconciliationRule rule) {

        List<CompletableFuture<List<ReconResult>>> futures =
                internalByCp.entrySet().stream()
                        .map(e -> {
                            Long cp = e.getKey();
                            List<TradeType> internalTrades = e.getValue();
                            List<TradeType> externalTrades = externalByCp.getOrDefault(cp, List.of());

                            return CompletableFuture.supplyAsync(
                                    () -> reconcile(internalTrades, externalTrades, rule)
                            );
                        })
                        .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        futures.stream()
                                .flatMap(f -> f.join().stream())
                                .toList()
                );
    }

    private ReconResult matchOne(TradeType internal, TradeType external, ReconciliationRule rule) {

        if (external == null) {
            return ReconResult.breakResult(
                    internal.tradeRef().value(),
                    "MISSING_EXTERNAL",
                    "No external trade found for " + internal.tradeRef().value()
            );
        }

        BigDecimal[] inVals = priceQty(internal);
        BigDecimal[] exVals = priceQty(external);

        double tolerance = reconConfig.getPriceTolerance();

        boolean matched = rule.matches(inVals[0], exVals[0], tolerance)
                && inVals[1].compareTo(exVals[1]) == 0;

        if (matched) {
            return ReconResult.matched(internal.tradeRef().value());
        }

        return ReconResult.breakResult(
                internal.tradeRef().value(),
                "VALUE_MISMATCH",
                "Internal=" + inVals[0] + "/" + inVals[1] +
                        ", External=" + exVals[0] + "/" + exVals[1]
        );
    }

    private BigDecimal[] priceQty(TradeType t) {
        return switch (t) {
            case EquityTrade eq -> new BigDecimal[]{eq.price(), eq.quantity()};
            case FXTrade fx -> new BigDecimal[]{fx.rate(), fx.amount()};
            case BondTrade bond -> new BigDecimal[]{bond.cleanPrice(), bond.quantity()};
            case DerivativeTrade der -> new BigDecimal[]{der.strikePrice(), der.notional()};
        };
    }
}

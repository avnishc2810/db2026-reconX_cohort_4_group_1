package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.EquityTrade;
import com.dbtraining.reconx.model.ReconciliationRule;
import com.dbtraining.reconx.model.Side;
import com.dbtraining.reconx.model.TradeRef;
import com.dbtraining.reconx.model.TradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class ReconciliationEngineTest {

    private ReconciliationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ReconciliationEngine();
    }

    @Test
    void reconcile_exactMatch_returnsMatched() {

        TradeType internal = equity(
                "EQU-20260602-0001",
                "100",
                "10"
        );

        TradeType external = equity(
                "EQU-20260602-0001",
                "100",
                "10"
        );

        List<ReconResult> results = engine.reconcile(
                List.of(internal),
                List.of(external),
                ReconciliationRule.EXACT
        );

        assertThat(results).hasSize(1);

        ReconResult result = results.get(0);

        assertThat(result.tradeRef()).isEqualTo("EQU-20260602-0001");
        assertThat(result.status()).isEqualTo(ReconResult.Status.MATCHED);
        assertThat(result.discrepancyType()).isNull();
        assertThat(result.details()).isNull();
    }

    @Test
    void reconcile_missingExternal_returnsBreak() {

        TradeType internal = equity(
                "EQU-20260602-0002",
                "100",
                "10"
        );

        List<ReconResult> results = engine.reconcile(
                List.of(internal),
                List.of(),
                ReconciliationRule.EXACT
        );

        assertThat(results).hasSize(1);

        ReconResult result = results.get(0);

        assertThat(result.status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(result.discrepancyType()).isEqualTo("MISSING_EXTERNAL");
        assertThat(result.details()).contains("No external trade found");
    }

    @Test
    void reconcile_valueMismatch_returnsBreak() {

        TradeType internal = equity(
                "EQU-20260602-0003",
                "100",
                "10"
        );

        TradeType external = equity(
                "EQU-20260602-0003",
                "100",
                "12"
        );

        List<ReconResult> results = engine.reconcile(
                List.of(internal),
                List.of(external),
                ReconciliationRule.EXACT
        );

        assertThat(results).hasSize(1);

        ReconResult result = results.get(0);

        assertThat(result.status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(result.discrepancyType()).isEqualTo("VALUE_MISMATCH");
        assertThat(result.details()).contains("internal=");
    }

    @Test
    void reconcile_emptyInternal_returnsEmptyList() {

        List<ReconResult> results = engine.reconcile(
                List.of(),
                List.of(),
                ReconciliationRule.EXACT
        );

        assertThat(results).isEmpty();
    }

    @Test
    void reconcile_nullInternal_returnsEmptyList() {

        List<ReconResult> results = engine.reconcile(
                null,
                List.of(),
                ReconciliationRule.EXACT
        );

        assertThat(results).isEmpty();
    }

    @Test
    void reconcile_nullExternal_returnsBreak() {

        TradeType internal = equity(
                "EQU-20260602-0004",
                "100",
                "10"
        );

        List<ReconResult> results = engine.reconcile(
                List.of(internal),
                null,
                ReconciliationRule.EXACT
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(results.get(0).discrepancyType()).isEqualTo("MISSING_EXTERNAL");
    }

    @Test
    void reconcileByCounterparty_matchesInParallel()
            throws ExecutionException, InterruptedException {

        TradeType trade = equity(
                "EQU-20260602-0005",
                "100",
                "10"
        );

        Map<Long, List<TradeType>> internal = Map.of(
                1L, List.of(trade)
        );

        Map<Long, List<TradeType>> external = Map.of(
                1L, List.of(trade)
        );

        List<ReconResult> results = engine.reconcileByCounterparty(
                internal,
                external,
                ReconciliationRule.EXACT
        ).get();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
    }

    private EquityTrade equity(String ref, String qty, String price) {

        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal(qty))
                .price(new BigDecimal(price))
                .currency("EUR")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();
    }
}
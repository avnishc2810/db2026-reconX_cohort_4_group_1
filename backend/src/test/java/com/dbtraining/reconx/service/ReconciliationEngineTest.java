package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV040 / ADV041 / ADV042 — TDD: write the test FIRST, then the impl.
 */
class ReconciliationEngineTest {

    private final ReconciliationEngine engine = new ReconciliationEngine();

    @Test
    void testReconcile_exactMatch_returnsMatched() {
        EquityTrade trade = equity("EQU-20260603-0001", "100.00", "1000");
        List<ReconResult> out = engine.reconcile(List.of(trade), List.of(trade), ReconciliationRule.EXACT);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
    }

    @Test
    void testReconcile_priceTolerance_withinThreshold() {
        EquityTrade internal = equity("EQU-20260603-0002", "100.00", "1000");
        EquityTrade external = equity("EQU-20260603-0002", "100.50", "1000");
        List<ReconResult> out = engine.reconcile(List.of(internal), List.of(external), ReconciliationRule.PRICE_TOLERANCE_1PCT);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
    }

    @Test
    void testReconcile_missingCounterpartyTrade_returnsBreak() {
        EquityTrade internal = equity("EQU-20260603-0003", "100.00", "1000");
        List<ReconResult> out = engine.reconcile(List.of(internal), List.of(), ReconciliationRule.EXACT);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(out.get(0).discrepancyType()).isEqualTo("MISSING_EXTERNAL");
    }

    @Test
    void testReconcile_emptyInternal_returnsEmpty() {
        assertThat(engine.reconcile(List.of(), List.of(), ReconciliationRule.EXACT)).isEmpty();
        assertThat(engine.reconcile(null, List.of(), ReconciliationRule.EXACT)).isEmpty();
    }

    @Test
    void testReconcile_singleInternalNoExternal_returnsBreak() {
        EquityTrade internal = equity("EQU-20260603-0004", "100.00", "1000");
        List<ReconResult> out = engine.reconcile(List.of(internal), List.of(), ReconciliationRule.EXACT);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.BREAK);
        assertThat(out.get(0).discrepancyType()).isEqualTo("MISSING_EXTERNAL");
    }

    @Test
    void testReconcile_allMismatched_summaryShowsZeroMatched() {
        List<TradeType> internals = List.of(
                equity("EQU-20260603-0005", "100.00", "1000"),
                equity("EQU-20260603-0006", "100.00", "1000"),
                equity("EQU-20260603-0007", "100.00", "1000"));
        List<TradeType> externals = List.of(
                equity("EQU-20260603-0005", "200.00", "1000"),
                equity("EQU-20260603-0006", "200.00", "1000"),
                equity("EQU-20260603-0007", "200.00", "1000"));

        List<ReconResult> out = engine.reconcile(internals, externals, ReconciliationRule.EXACT);

        long matchedCount = out.stream().filter(r -> r.status() == ReconResult.Status.MATCHED).count();
        long brokenCount = out.stream().filter(r -> r.status() == ReconResult.Status.BREAK).count();

        assertThat(out).hasSize(3);
        assertThat(matchedCount).isEqualTo(0);
        assertThat(brokenCount).isEqualTo(3);
    }

    private EquityTrade equity(String ref, String price, String qty) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(qty))
                .currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}

package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReconciliationRuleTest {

    @Test
    void exact_samePriceAndQuantity_matches() {
        assertThat(ReconciliationRule.EXACT.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100"),
                new BigDecimal("10")
        )).isTrue();
    }

    @Test
    void exact_priceDrift_doesNotMatch() {
        assertThat(ReconciliationRule.EXACT.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100.01"),
                new BigDecimal("10")
        )).isFalse();
    }

    @Test
    void exact_quantityDrift_doesNotMatch() {
        assertThat(ReconciliationRule.EXACT.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100"),
                new BigDecimal("11")
        )).isFalse();
    }

    @Test
    void priceTolerance1Pct_withinTolerance_matches() {
        assertThat(ReconciliationRule.PRICE_TOLERANCE_1PCT.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100.5"),
                new BigDecimal("10")
        )).isTrue();
    }

    @Test
    void priceTolerance1Pct_outsideTolerance_fails() {
        assertThat(ReconciliationRule.PRICE_TOLERANCE_1PCT.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("102"),
                new BigDecimal("10")
        )).isFalse();
    }

    @Test
    void qtyTolerance5Units_withinTolerance_matches() {
        assertThat(ReconciliationRule.QTY_TOLERANCE_5UNITS.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100"),
                new BigDecimal("14")
        )).isTrue();
    }

    @Test
    void qtyTolerance5Units_outsideTolerance_fails() {
        assertThat(ReconciliationRule.QTY_TOLERANCE_5UNITS.matches(
                new BigDecimal("100"),
                new BigDecimal("10"),
                new BigDecimal("100"),
                new BigDecimal("16")
        )).isFalse();
    }

    @Test
    void looseRule_allowsPriceAndQuantityDrift() {
        assertThat(ReconciliationRule.LOOSE.matches(
                new BigDecimal("100"),
                new BigDecimal("100"),
                new BigDecimal("104"),
                new BigDecimal("108")
        )).isTrue();
    }

    @Test
    void looseRule_priceTooLarge_fails() {
        assertThat(ReconciliationRule.LOOSE.matches(
                new BigDecimal("100"),
                new BigDecimal("100"),
                new BigDecimal("106"),
                new BigDecimal("108")
        )).isFalse();
    }

    @Test
    void looseRule_quantityTooLarge_fails() {
        assertThat(ReconciliationRule.LOOSE.matches(
                new BigDecimal("100"),
                new BigDecimal("100"),
                new BigDecimal("104"),
                new BigDecimal("111")
        )).isFalse();
    }

    @Test
    void zeroInternalPrice_isHandledWithoutDivideByZero() {
        assertThat(ReconciliationRule.EXACT.matches(
                BigDecimal.ZERO,
                new BigDecimal("10"),
                BigDecimal.ZERO,
                new BigDecimal("10")
        )).isTrue();

        assertThat(ReconciliationRule.EXACT.matches(
                BigDecimal.ZERO,
                new BigDecimal("10"),
                new BigDecimal("1"),
                new BigDecimal("10")
        )).isFalse();
    }
}
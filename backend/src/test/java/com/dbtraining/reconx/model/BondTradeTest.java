package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BondTradeTest {

    @Test
    void builder_buildsWhenAllRequiredPresent() {
        BondTrade trade = sampleBondTrade();

        assertThat(trade.tradeRef())
                .isEqualTo(TradeRef.of("BND-20260603-0001"));

        assertThat(trade.notional().amount())
                .isEqualByComparingTo("1000000");

        assertThat(trade.notional().currency().getCurrencyCode())
                .isEqualTo("USD");

        assertThat(trade.assetClass())
                .isEqualTo(TradeType.AssetClass.BOND);
    }

    @Test
    void builder_maturityBeforeTradeDate_throws() {
        assertThatThrownBy(() ->
                BondTrade.builder()
                        .tradeRef(TradeRef.of("BND-20260603-0001"))
                        .isin("US0378331005")
                        .faceValue(new BigDecimal("1000000"))
                        .couponRate(new BigDecimal("5.25"))
                        .currency("USD")
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 3))
                        .maturityDate(LocalDate.of(2026, 6, 2))
                        .counterpartyId(1L)
                        .build()
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maturityDate cannot be before tradeDate");
    }

    @Test
    void builder_missingFaceValue_throws() {
        assertThatThrownBy(() ->
                BondTrade.builder()
                        .tradeRef(TradeRef.of("BND-20260603-0001"))
                        .isin("US0378331005")
                        .couponRate(new BigDecimal("5.25"))
                        .currency("USD")
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 3))
                        .maturityDate(LocalDate.of(2031, 6, 3))
                        .counterpartyId(1L)
                        .build()
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("faceValue");
    }

    @Test
    void equality_byTradeRef() {
        BondTrade t1 = sampleBondTrade();

        BondTrade t2 = BondTrade.builder()
                .tradeRef(TradeRef.of("BND-20260603-0001"))
                .isin("US5949181045")
                .faceValue(new BigDecimal("500000"))
                .couponRate(new BigDecimal("4.75"))
                .currency("EUR")
                .side(Side.SELL)
                .tradeDate(LocalDate.of(2026, 6, 5))
                .maturityDate(LocalDate.of(2030, 6, 5))
                .counterpartyId(2L)
                .build();

        BondTrade t3 = BondTrade.builder()
                .tradeRef(TradeRef.of("BND-20260603-0002"))
                .isin("US5949181045")
                .faceValue(new BigDecimal("500000"))
                .couponRate(new BigDecimal("4.75"))
                .currency("EUR")
                .side(Side.SELL)
                .tradeDate(LocalDate.of(2026, 6, 5))
                .maturityDate(LocalDate.of(2030, 6, 5))
                .counterpartyId(2L)
                .build();

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        assertThat(t1).isNotEqualTo(t3);
    }

    /**
     * Enable this test only if you choose to validate ISIN length
     * in the builder (ADV021). If you defer ISIN validation to
     * Bean Validation (ADV029), leave this test disabled or remove it.
     */
    // @Test
    void builder_invalidIsinLength_throws() {
        assertThatThrownBy(() ->
                BondTrade.builder()
                        .tradeRef(TradeRef.of("BND-20260603-0001"))
                        .isin("US123") // invalid length
                        .faceValue(new BigDecimal("1000000"))
                        .couponRate(new BigDecimal("5"))
                        .currency("USD")
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 3))
                        .maturityDate(LocalDate.of(2031, 6, 3))
                        .counterpartyId(1L)
                        .build()
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ISIN");
    }

    private BondTrade sampleBondTrade() {
        return BondTrade.builder()
                .tradeRef(TradeRef.of("BND-20260603-0001"))
                .isin("US0378331005")
                .faceValue(new BigDecimal("1000000"))
                .couponRate(new BigDecimal("5.25"))
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .maturityDate(LocalDate.of(2031, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
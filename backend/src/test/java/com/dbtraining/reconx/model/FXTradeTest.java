package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FXTradeTest {

    @Test
    void builder_buildsWhenAllRequiredPresent() {
        FXTrade trade = sampleFxTrade();

        assertThat(trade.tradeRef())
                .isEqualTo(TradeRef.of("FXT-20260603-0001"));

        // 100000 * 1.12 = 112000.00 USD
        assertThat(trade.notional().amount())
                .isEqualByComparingTo("112000.00");

        assertThat(trade.notional().currency().getCurrencyCode())
                .isEqualTo("USD");

        assertThat(trade.assetClass())
                .isEqualTo(TradeType.AssetClass.FX);
    }

    @Test
    void builder_invalidIsoCode_throwsImmediately() {
        assertThatThrownBy(() ->
                FXTrade.builder()
                        .tradeRef(TradeRef.of("FXT-20260603-0001"))
                        .ccy1("EURR")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void builder_sameCurrencies_throws() {
        assertThatThrownBy(() ->
                FXTrade.builder()
                        .tradeRef(TradeRef.of("FXT-20260603-0001"))
                        .ccy1("EUR")
                        .ccy2("EUR")
                        .notionalCcy1(new BigDecimal("100000"))
                        .fxRate(new BigDecimal("1.12"))
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 3))
                        .counterpartyId(1L)
                        .build()
        )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("ccy1 and ccy2 must differ");
    }

    @Test
    void builder_nonPositiveFxRate_throws() {
        assertThatThrownBy(() ->
                FXTrade.builder()
                        .tradeRef(TradeRef.of("FXT-20260603-0001"))
                        .ccy1("EUR")
                        .ccy2("USD")
                        .notionalCcy1(new BigDecimal("100000"))
                        .fxRate(BigDecimal.ZERO)
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 3))
                        .counterpartyId(1L)
                        .build()
        )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("fxRate");
    }


    @Test
    void equality_byTradeRef() {
        FXTrade t1 = sampleFxTrade();

        FXTrade t2 = FXTrade.builder()
                .tradeRef(TradeRef.of("FXT-20260603-0001"))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("50000"))
                .fxRate(new BigDecimal("1.15"))
                .side(Side.SELL)
                .tradeDate(LocalDate.of(2026, 6, 4))
                .counterpartyId(2L)
                .build();

        FXTrade t3 = FXTrade.builder()
                .tradeRef(TradeRef.of("FXT-20260603-0002"))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.12"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        assertThat(t1).isNotEqualTo(t3);
    }

    private FXTrade sampleFxTrade() {
        return FXTrade.builder()
                .tradeRef(TradeRef.of("FXT-20260603-0001"))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.12"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
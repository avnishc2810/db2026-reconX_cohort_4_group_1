package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeEqualityTest {

    @Test
    void equityTrades_sameTradeRef_areEqual() {
        EquityTrade t1 = equity("EQU-20260602-0001", "100", "50");
        EquityTrade t2 = equity("EQU-20260602-0001", "200", "75");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        HashSet<TradeType> set = new HashSet<>(List.of(t1, t2));
        assertThat(set).hasSize(1);

        assertThat(t1.compareTo(t2)).isZero();
    }

    @Test
    void fxTrades_sameTradeRef_areEqual() {
        FXTrade t1 = fx("FXT-20260602-0001", "100000", "1.12");
        FXTrade t2 = fx("FXT-20260602-0001", "200000", "1.30");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        HashSet<TradeType> set = new HashSet<>(List.of(t1, t2));
        assertThat(set).hasSize(1);

        assertThat(t1.compareTo(t2)).isZero();
    }

    @Test
    void bondTrades_sameTradeRef_areEqual() {
        BondTrade t1 = bond("BND-20260602-0001", "100000");
        BondTrade t2 = bond("BND-20260602-0001", "500000");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        HashSet<TradeType> set = new HashSet<>(List.of(t1, t2));
        assertThat(set).hasSize(1);

        assertThat(t1.compareTo(t2)).isZero();
    }

    @Test
    void derivativeTrades_sameTradeRef_areEqual() {
        DerivativeTrade t1 = derivative("DRV-20260602-0001", "10");
        DerivativeTrade t2 = derivative("DRV-20260602-0001", "20");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        HashSet<TradeType> set = new HashSet<>(List.of(t1, t2));
        assertThat(set).hasSize(1);

        assertThat(t1.compareTo(t2)).isZero();
    }

    @Test
    void crossTypeTrades_sameTradeRef_areNotEqual() {
        TradeRef ref = TradeRef.of("EQU-20260602-9999");

        EquityTrade equity = EquityTrade.builder()
                .tradeRef(ref)
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("50"))
                .currency("EUR")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();

        FXTrade fx = FXTrade.builder()
                .tradeRef(ref)
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.10"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();

        assertThat(equity).isNotEqualTo(fx);
        assertThat(fx).isNotEqualTo(equity);
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

    private FXTrade fx(String ref, String notional, String rate) {
        return FXTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal(notional))
                .fxRate(new BigDecimal(rate))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();
    }

    private BondTrade bond(String ref, String faceValue) {
        return BondTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .isin("US0378331005")
                .faceValue(new BigDecimal(faceValue))
                .couponRate(new BigDecimal("0.05"))
                .maturityDate(LocalDate.of(2030, 6, 2))
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();
    }

    private DerivativeTrade derivative(String ref, String quantity) {
        return DerivativeTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .underlying("AAPL")
                .strike(new BigDecimal("150"))
                .quantity(new BigDecimal(quantity))
                .expiry(LocalDate.of(2027, 6, 2))
                .optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();
    }
}
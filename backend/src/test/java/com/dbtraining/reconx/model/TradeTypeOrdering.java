package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class TradeTypeOrderingTest {

    @Test
    void treeSet_ordersNewestTradeFirst() {
        TreeSet<TradeType> trades = new TreeSet<>();

        TradeType equity = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));
        TradeType fx = fx("FXT-20260603-0001", LocalDate.of(2026, 6, 3));
        TradeType bond = bond("BND-20260602-0001", LocalDate.of(2026, 6, 2));
        TradeType derivative = derivative("DER-20260604-0001", LocalDate.of(2026, 6, 4));

        trades.add(equity);
        trades.add(fx);
        trades.add(bond);
        trades.add(derivative);

        assertThat(trades)
                .extracting(TradeType::tradeDate)
                .containsExactly(
                        LocalDate.of(2026, 6, 4),
                        LocalDate.of(2026, 6, 3),
                        LocalDate.of(2026, 6, 2),
                        LocalDate.of(2026, 6, 1)
                );
    }

    @Test
    void sameTradeDate_ordersByTradeRefAscending() {
        TreeSet<TradeType> trades = new TreeSet<>();

        TradeType t2 = equity("EQU-20260601-0002", LocalDate.of(2026, 6, 1));
        TradeType t1 = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));

        trades.add(t2);
        trades.add(t1);

        assertThat(trades)
                .extracting(t -> t.tradeRef().value())
                .containsExactly(
                        "EQU-20260601-0001",
                        "EQU-20260601-0002"
                );
    }

    @Test
    void compareTo_sameTrade_returnsZero() {
        TradeType t1 = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));
        TradeType t2 = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));

        assertThat(t1.compareTo(t2)).isZero();
    }

    @Test
    void compareTo_differentTradeRef_notZero() {
        TradeType t1 = equity("EQU-20260601-0001", LocalDate.of(2026, 6, 1));
        TradeType t2 = equity("EQU-20260601-0002", LocalDate.of(2026, 6, 1));

        assertThat(t1.compareTo(t2)).isNotZero();
    }

    private EquityTrade equity(String ref, LocalDate date) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("100"))
                .currency("EUR")
                .side(Side.BUY)
                .tradeDate(date)
                .counterpartyId(1L)
                .build();
    }

    private FXTrade fx(String ref, LocalDate date) {
        return FXTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.12"))
                .side(Side.BUY)
                .tradeDate(date)
                .counterpartyId(1L)
                .build();
    }

    private BondTrade bond(String ref, LocalDate date) {
        return BondTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .isin("US0378331005")
                .faceValue(new BigDecimal("100000"))
                .couponRate(new BigDecimal("0.05"))
                .maturityDate(date.plusYears(5))
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(date)
                .counterpartyId(1L)
                .build();
    }

    private DerivativeTrade derivative(String ref, LocalDate date) {
        return DerivativeTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .underlying("AAPL")
                .strike(new BigDecimal("180"))
                .quantity(new BigDecimal("10"))
                .expiry(date.plusMonths(3))
                .optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(date)
                .counterpartyId(1L)
                .build();
    }
}
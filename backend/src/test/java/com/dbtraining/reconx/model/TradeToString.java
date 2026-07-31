package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TradeToStringTest {

    @Test
    void equityTrade_toString_containsBusinessFields_notPII() {
        EquityTrade trade = equity();

        String s = trade.toString();

        assertThat(s)
                .contains("EquityTrade")
                .contains(trade.tradeRef().toString())
                .contains(trade.instrumentSymbol())
                .contains(trade.quantity().toPlainString())
                .contains(trade.price().toPlainString())
                .contains(trade.currency().getCurrencyCode())
                .contains(trade.side().name())
                .doesNotContain(String.valueOf(trade.counterpartyId()));
    }

    @Test
    void fxTrade_toString_containsBusinessFields_notPII() {
        FXTrade trade = fx();

        String s = trade.toString();

        assertThat(s)
                .contains("FXTrade")
                .contains(trade.tradeRef().toString())
                .contains(trade.ccy1().getCurrencyCode())
                .contains(trade.ccy2().getCurrencyCode())
                .contains(trade.notionalCcy1().toPlainString())
                .contains(trade.fxRate().toPlainString())
                .contains(trade.side().name())
                .doesNotContain(String.valueOf(trade.counterpartyId()));
    }

    @Test
    void bondTrade_toString_containsBusinessFields_notPII() {
        BondTrade trade = bond();

        String s = trade.toString();

        assertThat(s)
                .contains("BondTrade")
                .contains(trade.tradeRef().toString())
                .contains(trade.isin())
                .contains(trade.faceValue().toPlainString())
                .contains(trade.currency().getCurrencyCode())
                .contains(trade.couponRate().toPlainString())
                .contains(trade.maturityDate().toString())
                .contains(trade.side().name())
                .doesNotContain(String.valueOf(trade.counterpartyId()));
    }

    @Test
    void derivativeTrade_toString_containsBusinessFields_notPII() {
        DerivativeTrade trade = derivative();

        String s = trade.toString();

        assertThat(s)
                .contains("DerivativeTrade")
                .contains(trade.tradeRef().toString())
                .contains(trade.underlying())
                .contains(trade.optionType().name())
                .contains(trade.strike().toPlainString())
                .contains(trade.currency().getCurrencyCode())
                .contains(trade.quantity().toPlainString())
                .contains(trade.expiry().toString())
                .contains(trade.side().name())
                .doesNotContain(String.valueOf(trade.counterpartyId()));
    }

    private EquityTrade equity() {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-0001"))
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("123.45"))
                .currency("EUR")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(9999L)
                .build();
    }

    private FXTrade fx() {
        return FXTrade.builder()
                .tradeRef(TradeRef.of("FXT-20260602-0001"))
                .ccy1("EUR")
                .ccy2("USD")
                .notionalCcy1(new BigDecimal("100000"))
                .fxRate(new BigDecimal("1.1200"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(9999L)
                .build();
    }

    private BondTrade bond() {
        return BondTrade.builder()
                .tradeRef(TradeRef.of("BND-20260602-0001"))
                .isin("US0378331005")
                .faceValue(new BigDecimal("100000"))
                .couponRate(new BigDecimal("0.05"))
                .maturityDate(LocalDate.of(2030, 6, 2))
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(9999L)
                .build();
    }

    private DerivativeTrade derivative() {
        return DerivativeTrade.builder()
                .tradeRef(TradeRef.of("DRV-20260602-0001"))
                .underlying("AAPL")
                .strike(new BigDecimal("180"))
                .quantity(new BigDecimal("10"))
                .expiry(LocalDate.of(2027, 6, 2))
                .optionType(DerivativeTrade.OptionType.CALL)
                .currency("USD")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(9999L)
                .build();
    }
}

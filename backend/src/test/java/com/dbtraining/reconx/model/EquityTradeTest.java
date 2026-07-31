package com.dbtraining.reconx.model;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquityTradeTest {

    @Test
    void builder_buildsWhenAllRequiredPresent() {
        // TODO(TICKET-ADV019): build an EquityTrade via the Builder with all required fields,
        //                     then assert tradeRef, notional (price*qty) and assetClass = EQUITY.
        EquityTrade trade = EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-0001"))
                .instrumentSymbol("AAPL")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("10"))
                .currency(Currency.getInstance("USD"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();

        assertEquals(
                TradeRef.of("EQU-20260602-0001"),
                trade.tradeRef()
        );

        assertEquals(
                EquityTrade.AssetClass.EQUITY,
                trade.assetClass()
        );

        assertEquals(
                new BigDecimal("1000"),
                trade.notional().amount()
        );

        assertEquals(
                Currency.getInstance("USD"),
                trade.notional().currency()
        );
    }

    @Test
    void builder_missingPrice_throws() {
        // TODO(TICKET-ADV019): omit .price(...) on the Builder and assert build() throws
        //                     NullPointerException whose message mentions "price".
        
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> EquityTrade.builder()
                        .tradeRef(TradeRef.of("EQU-20260602-0002"))
                        .instrumentSymbol("AAPL")
                        .quantity(new BigDecimal("100"))
                        // price intentionally omitted
                        .currency(Currency.getInstance("USD"))
                        .side(Side.BUY)
                        .tradeDate(LocalDate.of(2026, 6, 2))
                        .counterpartyId(1L)
                        .build()
        );

        assertTrue(ex.getMessage().contains("price"));
    }

    @Test
    void equality_byTradeRef() {
        // TODO(TICKET-ADV028): two EquityTrades with the same tradeRef are equal and share hashCode;
        //                     a third with a different tradeRef is not equal.
                EquityTrade t1 = EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-0003"))
                .instrumentSymbol("AAPL")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("10"))
                .currency(Currency.getInstance("USD"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();

        EquityTrade t2 = EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-0003"))
                .instrumentSymbol("MSFT")
                .quantity(new BigDecimal("500"))
                .price(new BigDecimal("20"))
                .currency(Currency.getInstance("EUR"))
                .side(Side.SELL)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(99L)
                .build();

        EquityTrade t3 = EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-0004"))
                .instrumentSymbol("AAPL")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("10"))
                .currency(Currency.getInstance("USD"))
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());

        assertNotEquals(t1, t3);
    }

    private EquityTrade sampleEquity(String ref) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("100"))
                .currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L).build();
    }
}

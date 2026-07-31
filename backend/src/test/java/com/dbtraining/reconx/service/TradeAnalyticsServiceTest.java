package com.dbtraining.reconx.service;

import com.dbtraining.reconx.model.EquityTrade;
import com.dbtraining.reconx.model.Side;
import com.dbtraining.reconx.model.TradeRef;
import com.dbtraining.reconx.model.TradeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

class TradeAnalyticsServiceTest {

    private final TradeAnalyticsService service = new TradeAnalyticsService();

    // ---------------------------------------------------------------------
    // Existing PnL Tests
    // ---------------------------------------------------------------------

    private EquityTrade trade(String symbol, Side side, int qty, BigDecimal price) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of("EQU-20260602-" + String.format("%04d", qty)))
                .instrumentSymbol(symbol)
                .quantity(BigDecimal.valueOf(qty))
                .price(price)
                .currency("USD")
                .side(side)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(1L)
                .build();
    }

    @Test
    void pnlByInstrument_mixedBuySell() {

        List<EquityTrade> trades = List.of(
                trade("AAPL", Side.BUY, 100, new BigDecimal("10")),
                trade("AAPL", Side.SELL, 100, new BigDecimal("12")),
                trade("MSFT", Side.SELL, 50, new BigDecimal("20")),
                trade("GOOG", Side.BUY, 10, new BigDecimal("100"))
        );

        Map<String, BigDecimal> result = service.pnlByInstrument(trades);

        assertEquals(new BigDecimal("200"), result.get("AAPL"));
        assertEquals(new BigDecimal("1000"), result.get("MSFT"));
        assertEquals(new BigDecimal("-1000"), result.get("GOOG"));
    }

    @Test
    void pnlByInstrument_parallelStream() {

        List<EquityTrade> trades = List.of(
                trade("AAPL", Side.BUY, 100, new BigDecimal("10")),
                trade("AAPL", Side.SELL, 100, new BigDecimal("12")),
                trade("MSFT", Side.SELL, 50, new BigDecimal("20")),
                trade("GOOG", Side.BUY, 10, new BigDecimal("100"))
        );

        Map<String, BigDecimal> sequential = service.pnlByInstrument(trades);

        Map<String, BigDecimal> parallel = trades.parallelStream().collect(
                Collectors.groupingBy(
                        EquityTrade::instrumentSymbol,
                        Collectors.mapping(
                                service::pnl,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                )
        );

        assertEquals(sequential, parallel);
    }

    // ---------------------------------------------------------------------
    // TICKET-ADV034 Tests
    // ---------------------------------------------------------------------

    @Test
    void notionalByCounterparty_groupsTradesCorrectly() {

        TradeType t1 = equity(
                "EQU-20260602-1001",
                1L,
                "100",
                "10"      // 1000
        );

        TradeType t2 = equity(
                "EQU-20260602-1002",
                1L,
                "50",
                "20"       // 1000
        );

        TradeType t3 = equity(
                "EQU-20260602-1003",
                2L,
                "30",
                "10"       // 300
        );

        Map<Long, TradeAnalyticsService.NotionalSummary> result =
                service.notionalByCounterparty(List.of(t1, t2, t3));

        assertThat(result).hasSize(2);

        assertThat(result.get(1L).count()).isEqualTo(2);
        assertThat(result.get(1L).total()).isEqualByComparingTo("2000");

        assertThat(result.get(2L).count()).isEqualTo(1);
        assertThat(result.get(2L).total()).isEqualByComparingTo("300");
    }

    @Test
    void notionalTotals_areExactBigDecimals() {

        TradeType t1 = equity(
                "EQU-20260602-2001",
                99L,
                "1.25",
                "2.40"       // 3.00
        );

        TradeType t2 = equity(
                "EQU-20260602-2002",
                99L,
                "2.50",
                "4.00"       // 10.00
        );

        Map<Long, TradeAnalyticsService.NotionalSummary> result =
                service.notionalByCounterparty(List.of(t1, t2));

        assertThat(result.get(99L).total())
                .isEqualByComparingTo("13.00");
    }
    
    @Test
    void emptyInput_returnsEmptyMap() {

        Map<Long, TradeAnalyticsService.NotionalSummary> result =
                service.notionalByCounterparty(List.of());

        assertThat(result).isEmpty();
    }
@Test
void vwap_singleInstrument() {
    List<EquityTrade> trades = List.of(
            EquityTrade.builder()
                    .tradeRef(TradeRef.of("EQU-20260601-0001"))
                    .instrumentSymbol("AAPL")
                    .quantity(new BigDecimal("100"))
                    .price(new BigDecimal("10"))
                    .currency("USD")
                    .side(Side.BUY)
                    .tradeDate(LocalDate.of(2026, 6, 1))
                    .counterpartyId(1L)
                    .build(),

            EquityTrade.builder()
                    .tradeRef(TradeRef.of("EQU-20260601-0002"))
                    .instrumentSymbol("AAPL")
                    .quantity(new BigDecimal("200"))
                    .price(new BigDecimal("20"))
                    .currency("USD")
                    .side(Side.BUY)
                    .tradeDate(LocalDate.of(2026, 6, 1))
                    .counterpartyId(1L)
                    .build()
    );

    Map<String, BigDecimal> result = service.vwapByInstrument(trades);

    // (100×10 + 200×20) / 300 = 16.6667
    assertEquals(new BigDecimal("16.6667"), result.get("AAPL"));
}

@Test
void vwap_parallelMatchesSequential() {
    List<EquityTrade> trades = List.of(
            EquityTrade.builder()
                    .tradeRef(TradeRef.of("EQU-20260601-0001"))
                    .instrumentSymbol("AAPL")
                    .quantity(new BigDecimal("100"))
                    .price(new BigDecimal("10"))
                    .currency("USD")
                    .side(Side.BUY)
                    .tradeDate(LocalDate.of(2026, 6, 1))
                    .counterpartyId(1L)
                    .build(),

            EquityTrade.builder()
                    .tradeRef(TradeRef.of("EQU-20260601-0002"))
                    .instrumentSymbol("AAPL")
                    .quantity(new BigDecimal("200"))
                    .price(new BigDecimal("20"))
                    .currency("USD")
                    .side(Side.BUY)
                    .tradeDate(LocalDate.of(2026, 6, 1))
                    .counterpartyId(1L)
                    .build(),

            EquityTrade.builder()
                    .tradeRef(TradeRef.of("EQU-20260601-0003"))
                    .instrumentSymbol("MSFT")
                    .quantity(new BigDecimal("50"))
                    .price(new BigDecimal("30"))
                    .currency("USD")
                    .side(Side.BUY)
                    .tradeDate(LocalDate.of(2026, 6, 1))
                    .counterpartyId(2L)
                    .build()
    );

    Map<String, BigDecimal> sequential = service.vwapByInstrument(trades);

    Map<String, List<EquityTrade>> grouped =
            trades.parallelStream().collect(Collectors.groupingBy(EquityTrade::instrumentSymbol));

    Map<String, BigDecimal> parallel = grouped.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> {
                BigDecimal totalQty = e.getValue().stream()
                        .map(EquityTrade::quantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal weighted = e.getValue().stream()
                        .map(t -> t.price().multiply(t.quantity()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                return weighted.divide(totalQty, 4, RoundingMode.HALF_UP);
            }
    ));

    assertEquals(sequential, parallel);
}

@Test
void vwap_emptyInput_returnsEmptyMap() {
    Map<String, BigDecimal> result = service.vwapByInstrument(List.of());

    assertTrue(result.isEmpty());
}
    @Test
    void summaryRecord_isImmutable() {

        TradeAnalyticsService.NotionalSummary summary =
                new TradeAnalyticsService.NotionalSummary(
                        5,
                        new BigDecimal("1234.56")
                );

        assertThat(summary.count()).isEqualTo(5);
        assertThat(summary.total()).isEqualByComparingTo("1234.56");
    }

    // ---------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------

    private EquityTrade equity(
            String ref,
            long counterpartyId,
            String quantity,
            String price) {

        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .quantity(new BigDecimal(quantity))
                .price(new BigDecimal(price))
                .currency("EUR")
                .side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 2))
                .counterpartyId(counterpartyId)
                .build();
    }
}
package com.dbtraining.reconx.service;

import com.dbtraining.reconx.model.EquityTrade;
import com.dbtraining.reconx.model.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeAnalyticsServiceTest {

    private final TradeAnalyticsService service = new TradeAnalyticsService();

    private EquityTrade trade(String symbol, Side side, int qty, BigDecimal price) {
        return EquityTrade.builder()
                .instrumentSymbol(symbol)
                .side(side)
                .quantity(BigDecimal.valueOf(qty))
                .price(price)
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

        assertEquals(new BigDecimal("200"), result.get("AAPL"));  // -1000 + 1200
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

        Map<String, BigDecimal> parallel = trades.parallelStream().collect(Collectors.groupingBy(
                EquityTrade::instrumentSymbol,
                Collectors.mapping(service::pnl,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
        ));

        assertEquals(sequential, parallel);
    }
}

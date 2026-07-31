package com.dbtraining.reconx.model;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TradeFactoryTest {

    @Test
    void create_equity_returnsEquityTrade() {
        Map<String, Object> map = Map.of(
                "tradeRef", "ABC-20260603-0001",
                "symbol", "SAP.DE",
                "quantity", new BigDecimal("100"),
                "price", new BigDecimal("125"),
                "currency", "EUR",
                "side", "BUY",
                "tradeDate", "2026-06-03",
                "counterpartyId", 1L
        );

        TradeType trade = TradeFactory.create("EQUITY", map);

        assertThat(trade)
                .isNotNull()
                .isInstanceOf(EquityTrade.class);
    }

    @Test
    void create_unknownAssetClass_throws() {
        assertThatThrownBy(() ->
                TradeFactory.create("FOO", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

   @Test
void create_missingPrice_throwsNullPointerException() {

    Map<String, Object> map = Map.of(
            "tradeRef", "ABC-20260603-0001",
            "symbol", "SAP.DE",
            "quantity", new BigDecimal("100"),
            // price intentionally omitted
            "currency", "EUR",
            "side", "BUY",
            "tradeDate", "2026-06-03",
            "counterpartyId", 1L
    );

    assertThatThrownBy(() -> TradeFactory.create("EQUITY", map))
            .isInstanceOf(NullPointerException.class);
}

    @Test
    void constructor_isPrivate() throws Exception {

        var constructor = TradeFactory.class.getDeclaredConstructor();

        assertThat(constructor.canAccess(null)).isFalse();

        constructor.setAccessible(true);

        Object instance = constructor.newInstance();

        assertThat(instance).isInstanceOf(TradeFactory.class);
    }

    @Test
    void allMethodsAreStatic() {

        assertThat(TradeFactory.class.getDeclaredMethods())
                .filteredOn(method -> !method.isSynthetic())
                .allMatch(method ->
                        java.lang.reflect.Modifier.isStatic(method.getModifiers())
                                || method.getName().equals("<init>")
                );
    }
}

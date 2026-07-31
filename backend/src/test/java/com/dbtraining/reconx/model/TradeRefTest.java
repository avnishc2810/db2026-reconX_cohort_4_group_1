package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TradeRefTest {

    @Test
    void validTradeRef_succeeds() {

        TradeRef ref = TradeRef.of("EQU-20260602-0001");

        assertThat(ref.value()).isEqualTo("EQU-20260602-0001");
        assertThat(ref.toString()).isEqualTo("EQU-20260602-0001");
    }

    @Test
    void invalidTradeRefFormat_throws() {

        assertThatThrownBy(() ->
                TradeRef.of("foo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AAA-YYYYMMDD-NNNN");
    }

    @Test
    void nullTradeRef_throws() {

        assertThatThrownBy(() ->
                TradeRef.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tradeRef value");
    }

    @Test
    void equality_sameValue() {

        TradeRef r1 = TradeRef.of("EQU-20260602-0001");
        TradeRef r2 = TradeRef.of("EQU-20260602-0001");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void differentTradeRefs_notEqual() {

        TradeRef r1 = TradeRef.of("EQU-20260602-0001");
        TradeRef r2 = TradeRef.of("EQU-20260602-0002");

        assertThat(r1).isNotEqualTo(r2);
    }
}
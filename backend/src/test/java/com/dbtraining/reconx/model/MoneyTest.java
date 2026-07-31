package com.dbtraining.reconx.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void equality_sameAmountAndCurrency() {
        Money m1 = Money.of("100", "USD");
        Money m2 = Money.of("100", "USD");

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }

    @Test
    void plus_sameCurrency_returnsNewMoney() {
        Money original = Money.of("100", "USD");
        Money other = Money.of("50", "USD");

        Money result = original.plus(other);

        assertThat(result.amount()).isEqualByComparingTo("150");
        assertThat(result.currency()).isEqualTo(Currency.getInstance("USD"));

        // Original objects unchanged
        assertThat(original.amount()).isEqualByComparingTo("100");
        assertThat(other.amount()).isEqualByComparingTo("50");
    }

    @Test
    void plus_currencyMismatch_throws() {
        Money usd = Money.of("100", "USD");
        Money eur = Money.of("50", "EUR");

        assertThatThrownBy(() -> usd.plus(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency mismatch");
    }

    @Test
    void times_returnsScaledMoney() {
        Money money = Money.of("100", "USD");

        Money result = money.times(new BigDecimal("2.5"));

        assertThat(result.amount()).isEqualByComparingTo("250.0");
        assertThat(result.currency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    void negativeAmount_throws() {
        assertThatThrownBy(() ->
                Money.of("-1", "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
}
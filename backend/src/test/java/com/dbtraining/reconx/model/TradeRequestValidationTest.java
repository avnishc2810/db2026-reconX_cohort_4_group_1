package com.dbtraining.reconx.model;

import com.dbtraining.reconx.dto.TradeRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TradeRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_hasNoViolations() {
        TradeRequest request = validRequest();

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void invalidTradeRef_reportsViolation() {
        TradeRequest request = new TradeRequest(
                "foo",
                1L,
                2L,
                "EQUITY",
                "BUY",
                new BigDecimal("100"),
                new BigDecimal("50"),
                LocalDate.now()
        );

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals("tradeRef")
                                && v.getMessage().contains("AAA-YYYYMMDD-NNNN"));
    }

    @Test
    void nullRequiredFields_reportViolations() {
        TradeRequest request = new TradeRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder(
                        "tradeRef",
                        "instrumentId",
                        "counterpartyId",
                        "assetClass",
                        "side",
                        "quantity",
                        "price",
                        "tradeDate"
                );
    }

    @Test
    void invalidSide_reportsViolation() {
        TradeRequest request = new TradeRequest(
                "EQU-20260602-0001",
                1L,
                2L,
                "EQUITY",
                "HOLD",
                new BigDecimal("100"),
                new BigDecimal("50"),
                LocalDate.now()
        );

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("side"));
    }

    @Test
    void negativeQuantity_reportsViolation() {
        TradeRequest request = new TradeRequest(
                "EQU-20260602-0001",
                1L,
                2L,
                "EQUITY",
                "BUY",
                new BigDecimal("-100"),
                new BigDecimal("50"),
                LocalDate.now()
        );

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity"));
    }

    @Test
    void negativePrice_reportsViolation() {
        TradeRequest request = new TradeRequest(
                "EQU-20260602-0001",
                1L,
                2L,
                "EQUITY",
                "BUY",
                new BigDecimal("100"),
                new BigDecimal("-1"),
                LocalDate.now()
        );

        Set<ConstraintViolation<TradeRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    private TradeRequest validRequest() {
        return new TradeRequest(
                "EQU-20260602-0001",
                1L,
                2L,
                "EQUITY",
                "BUY",
                new BigDecimal("100"),
                new BigDecimal("50"),
                LocalDate.of(2026, 6, 2)
        );
    }
}
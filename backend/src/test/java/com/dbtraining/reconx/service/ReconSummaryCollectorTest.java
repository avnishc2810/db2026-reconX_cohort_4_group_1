package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ReconSummaryCollectorTest {

    @Test
    void serialCollector_countsResultsCorrectly() {

        List<ReconResult> results = List.of(
                ReconResult.matched("T1"),
                ReconResult.matched("T2"),
                ReconResult.breakResult("T3", "PRICE", "Mismatch"),
                ReconResult.breakResult("T4", "QTY", "Mismatch"),
                ReconResult.matched("T5")
        );

        ReconSummary summary = results.stream()
                .collect(new ReconSummaryCollector());

        assertThat(summary.total()).isEqualTo(5);
        assertThat(summary.matched()).isEqualTo(3);
        assertThat(summary.broken()).isEqualTo(2);
    }

    @Test
    void parallelCollector_matchesSerialCollector() {

        List<ReconResult> results = IntStream.range(0, 10_000)
                .mapToObj(i ->
                        i % 2 == 0
                                ? ReconResult.matched("T" + i)
                                : ReconResult.breakResult("T" + i, "BREAK", "Mismatch"))
                .toList();

        ReconSummary serial = results.stream()
                .collect(new ReconSummaryCollector());

        ReconSummary parallel = results.parallelStream()
                .collect(new ReconSummaryCollector());

        assertThat(parallel).isEqualTo(serial);
    }

    @Test
    void emptyFactory_returnsZeroCounts() {

        ReconSummary summary = ReconSummary.empty();

        assertThat(summary.total()).isZero();
        assertThat(summary.matched()).isZero();
        assertThat(summary.broken()).isZero();
    }

    @Test
    void reconSummaryRecord_isImmutable() {

        ReconSummary summary = new ReconSummary(10, 7, 3);

        assertThat(summary.total()).isEqualTo(10);
        assertThat(summary.matched()).isEqualTo(7);
        assertThat(summary.broken()).isEqualTo(3);
    }

    @Test
    void emptyStream_collectsToZeroSummary() {

        ReconSummary summary = List.<ReconResult>of()
                .stream()
                .collect(new ReconSummaryCollector());

        assertThat(summary.total()).isZero();
        assertThat(summary.matched()).isZero();
        assertThat(summary.broken()).isZero();
    }
}
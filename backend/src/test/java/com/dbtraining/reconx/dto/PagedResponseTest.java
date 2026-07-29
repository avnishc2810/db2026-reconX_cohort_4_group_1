package com.dbtraining.reconx.dto;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV053 — Unit test for PagedResponse<T> DTO envelope.
 */
class PagedResponseTest {

    @Test
    void testPagedResponse_of_flattensPageCorrectly() {
        List<String> rawData = List.of("EQU-20260603-0001", "EQU-20260603-0002");
        Page<String> page = new PageImpl<>(rawData, PageRequest.of(0, 10), 2);

        PagedResponse<String> response = PagedResponse.of(page, Function.identity());

        assertThat(response.items()).containsExactly("EQU-20260603-0001", "EQU-20260603-0002");
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    void testPagedResponse_from_appliesMapperFunction() {
        List<String> rawData = List.of("alpha", "beta");
        Page<String> page = new PageImpl<>(rawData, PageRequest.of(1, 5), 12);

        PagedResponse<String> response = PagedResponse.from(page, String::toUpperCase);

        assertThat(response.items()).containsExactly("ALPHA", "BETA");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(12L);
        assertThat(response.totalPages()).isEqualTo(3);
    }
}

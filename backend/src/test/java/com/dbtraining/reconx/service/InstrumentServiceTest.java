package com.dbtraining.reconx.service;

import com.dbtraining.reconx.exception.InvalidTradeException;
import com.dbtraining.reconx.repository.InstrumentRepository;
import com.dbtraining.reconx.repository.entity.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * TICKET-ADV081 — Unit test for InstrumentService findBySymbol lookup.
 */
class InstrumentServiceTest {

    private InstrumentRepository repo;
    private InstrumentService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(InstrumentRepository.class);
        service = new InstrumentService(repo);
    }

    @Test
    void testFindBySymbol_whenFound_returnsInstrument() {
        Instrument mockInst = new Instrument();
        when(repo.findBySymbol("SAP.DE")).thenReturn(Optional.of(mockInst));

        Instrument result = service.findBySymbol("SAP.DE");
        assertThat(result).isSameAs(mockInst);
    }

    @Test
    void testFindBySymbol_whenNotFound_throwsInvalidTradeException() {
        when(repo.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findBySymbol("UNKNOWN"))
                .isInstanceOf(InvalidTradeException.class)
                .hasMessageContaining("Unknown instrument symbol: UNKNOWN");
    }
}

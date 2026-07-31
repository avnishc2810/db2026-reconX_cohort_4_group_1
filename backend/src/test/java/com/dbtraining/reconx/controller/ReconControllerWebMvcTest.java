package com.dbtraining.reconx.controller;

import com.dbtraining.reconx.repository.ReconBreakRepository;
import com.dbtraining.reconx.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TICKET-ADV069 — WebMvcTest for ReconController GET /v1/recon/jobs/{jobId}/results endpoint.
 */
@WebMvcTest(ReconController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReconControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReconBreakRepository reconBreakRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void testGetJobResults_returns200AndJsonList() throws Exception {
        Mockito.when(reconBreakRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/v1/recon/jobs/job-1234/results")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        Mockito.verify(reconBreakRepository).findAll();
    }
}

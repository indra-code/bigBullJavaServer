package com.BigBull.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.BigBull.service.PortfolioService;

@WebMvcTest(PortfolioController.class)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void testGetPortfolioSummary_Returns200WithSummary() throws Exception {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalInvested", 1000.0);
        summary.put("totalCurrentValue", 1100.0);
        summary.put("totalPnl", 100.0);
        when(portfolioService.getPortfolioSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/portfolio/summary"))
            .andExpect(status().isOk());
    }
}

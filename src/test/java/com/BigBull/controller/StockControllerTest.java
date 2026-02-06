package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasKey;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockController stockController;

    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(stockController, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(stockController, "FLASK_BASE_URL", "http://localhost:5000");
    }

    @Test
    void testSearchStocks_Returns200() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("results", new Object[] {"RELIANCE"});
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/stocks/search")
                .param("query", "rel")
                .param("maxResults", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0]", is("RELIANCE")));
    }

    @Test
    void testGetStockQuote_Returns200() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("price", 250.0);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/stocks/quote/RELIANCE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price", is(250.0)));
    }

    @Test
    void testGetStockHistory_Returns200() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("history", new Object[] {1, 2, 3});
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/stocks/history/RELIANCE")
                .param("timeframe", "1M"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.history").exists());
    }

    @Test
    void testGetStockInfo_Returns200() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", "RELIANCE");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/stocks/info/RELIANCE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol", is("RELIANCE")));
    }
}

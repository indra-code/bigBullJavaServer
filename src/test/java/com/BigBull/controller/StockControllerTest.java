package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            .andExpect(status().isOk());
    }

    @Test
    void testGetStockQuote_Returns200() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("price", 250.0);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/stocks/quote/RELIANCE"))
            .andExpect(status().isOk());
    }
}

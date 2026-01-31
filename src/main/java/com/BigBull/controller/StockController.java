package com.BigBull.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://localhost:5000";
    
    @GetMapping("/search")
    public ResponseEntity<Map> searchStocks(@RequestParam String query,
                                           @RequestParam(defaultValue = "5") int maxResults) {
        String url = FLASK_BASE_URL + "/api/search?query=" + query + "&max_results=" + maxResults;
        Map response = restTemplate.getForObject(url, Map.class);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{symbol}")
    public ResponseEntity<Map> getStockHistory(@PathVariable String symbol,
                                               @RequestParam(defaultValue = "1M") String timeframe) {
        String url = FLASK_BASE_URL + "/api/stock/history/" + symbol + "?timeframe=" + timeframe;
        Map response = restTemplate.getForObject(url, Map.class);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info/{symbol}")
    public ResponseEntity<Map> getStockInfo(@PathVariable String symbol) {
        String url = FLASK_BASE_URL + "/api/stock/info/" + symbol;
        Map response = restTemplate.getForObject(url, Map.class);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/quote/{symbol}")
    public ResponseEntity<Map> getStockQuote(@PathVariable String symbol) {
        String url = FLASK_BASE_URL + "/api/stock/quote/" + symbol;
        Map response = restTemplate.getForObject(url, Map.class);
        return ResponseEntity.ok(response);
    }
}

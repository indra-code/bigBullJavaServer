package com.BigBull.service;

import com.BigBull.dto.AssetSummary;
import com.BigBull.entity.Asset;
import com.BigBull.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String PYTHON_API_URL = "http://localhost:5000/api";

    public List<Asset> searchAssets(String query) {
        return assetRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }

    public Asset addOrUpdateAsset(Asset incoming) {
        Asset existing = assetRepository.findBySymbol(incoming.getSymbol())
                .orElse(null);

        if (existing == null) {
            incoming.setCreatedAt(LocalDateTime.now());
            incoming.setUpdatedAt(LocalDateTime.now());
            return assetRepository.save(incoming);
        }

        // Calculate weighted average cost
        double existingValue = existing.getQuantity() * existing.getCostPerUnit();
        double incomingValue = incoming.getQuantity() * incoming.getCostPerUnit();
        double newQuantity = existing.getQuantity() + incoming.getQuantity();
        double newAvgCost = (existingValue + incomingValue) / newQuantity;

        existing.setQuantity(newQuantity);
        existing.setCostPerUnit(newAvgCost);
        existing.setUpdatedAt(LocalDateTime.now());

        return assetRepository.save(existing);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }

    public Optional<Asset> getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol);
    }

    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }

    public AssetSummary getAssetSummary(Asset asset) {
        try {
            double currentPrice = getCurrentPrice(asset.getSymbol(), asset.getType());
            double totalValue = currentPrice * asset.getQuantity();
            double totalCostValue = asset.getCostPerUnit() * asset.getQuantity();
            double unrealizedGain = totalValue - totalCostValue;
            double gainPercentage = (totalCostValue > 0) ? (unrealizedGain / totalCostValue) * 100 : 0;

            return new AssetSummary(
                    asset.getId(),
                    asset.getSymbol(),
                    asset.getName(),
                    asset.getType(),
                    asset.getQuantity(),
                    asset.getCostPerUnit(),
                    currentPrice,
                    totalValue,
                    totalCostValue,
                    unrealizedGain,
                    gainPercentage
            );
        } catch (Exception e) {
            double totalValue = asset.getCostPerUnit() * asset.getQuantity();
            return new AssetSummary(
                    asset.getId(),
                    asset.getSymbol(),
                    asset.getName(),
                    asset.getType(),
                    asset.getQuantity(),
                    asset.getCostPerUnit(),
                    asset.getCostPerUnit(),
                    totalValue,
                    totalValue,
                    0,
                    0
            );
        }
    }

    private double getCurrentPrice(String symbol, String assetType) {
        try {
            String endpoint = (assetType != null && assetType.equalsIgnoreCase("CRYPTO"))
                    ? "/crypto/quote/" + symbol
                    : "/stock/quote/" + symbol;

            String url = PYTHON_API_URL + endpoint;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("price")) {
                Object priceObj = response.get("price");
                if (priceObj instanceof Number) {
                    return ((Number) priceObj).doubleValue();
                } else if (priceObj instanceof String) {
                    return Double.parseDouble((String) priceObj);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching price for " + symbol + ": " + e.getMessage());
        }
        return 0.0;
    }
}

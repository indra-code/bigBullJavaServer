package com.BigBull.service;

import com.BigBull.entity.Asset;
import com.BigBull.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;
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
}

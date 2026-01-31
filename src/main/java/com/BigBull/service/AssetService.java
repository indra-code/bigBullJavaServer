package com.BigBull.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BigBull.entity.Asset;
import com.BigBull.repository.AssetRepository;

@Service
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    
    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }
    
    public Optional<Asset> getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol);
    }
    
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }
    
    public Asset createAsset(String type, String name, String symbol) {
        if (assetRepository.existsBySymbol(symbol)) {
            return assetRepository.findBySymbol(symbol).get();
        }
        Asset asset = new Asset(type, name, symbol);
        return assetRepository.save(asset);
    }
    
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
}

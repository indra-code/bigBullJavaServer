package com.BigBull.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BigBull.entity.Asset;
import com.BigBull.exception.AssetNotFoundException;
import com.BigBull.exception.InvalidAssetQuantityException;
import com.BigBull.repository.AssetRepository;

@Service
public class AssetService {
    
    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);
    
    @Autowired
    private AssetRepository assetRepository;
    
    public List<Asset> getAllAssets() {
        logger.debug("Fetching all assets");
        try {
            List<Asset> assets = assetRepository.findAll();
            logger.debug("Retrieved {} assets from database", assets.size());
            return assets;
        } catch (Exception e) {
            logger.error("Error retrieving all assets", e);
            throw e;
        }
    }
    
    public Optional<Asset> getAssetById(Long id) {
        logger.debug("Fetching asset by ID: {}", id);
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid asset ID provided: {}", id);
                throw new AssetNotFoundException("Invalid asset ID: " + id, id);
            }
            Optional<Asset> asset = assetRepository.findById(id);
            if (asset.isPresent()) {
                logger.debug("Asset found with ID: {}, Symbol: {}, Quantity: {}", 
                    id, asset.get().getSymbol(), asset.get().getQuantity());
            } else {
                logger.warn("Asset not found with ID: {}", id);
            }
            return asset;
        } catch (AssetNotFoundException e) {
            logger.error("Error retrieving asset by ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving asset by ID: {}", id, e);
            throw e;
        }
    }
    
    public Optional<Asset> getAssetBySymbol(String symbol) {
        logger.debug("Fetching asset by symbol: {}", symbol);
        try {
            if (symbol == null || symbol.trim().isEmpty()) {
                logger.error("Invalid symbol provided: {}", symbol);
                throw new AssetNotFoundException("Invalid asset symbol: " + symbol, null, symbol);
            }
            Optional<Asset> asset = assetRepository.findBySymbol(symbol);
            if (asset.isPresent()) {
                logger.debug("Asset found with symbol: {}, ID: {}, Quantity: {}", 
                    symbol, asset.get().getId(), asset.get().getQuantity());
            } else {
                logger.warn("Asset not found with symbol: {}", symbol);
            }
            return asset;
        } catch (AssetNotFoundException e) {
            logger.error("Error retrieving asset by symbol: {}", symbol, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving asset by symbol: {}", symbol, e);
            throw e;
        }
    }
    
    public Asset saveAsset(Asset asset) {
        logger.debug("Saving asset with symbol: {}, quantity: {}", 
            asset.getSymbol(), asset.getQuantity());
        try {
            Asset savedAsset = assetRepository.save(asset);
            logger.info("Asset saved successfully. ID: {}, Symbol: {}, Quantity: {}, Cost Per Unit: {}", 
                savedAsset.getId(), savedAsset.getSymbol(), savedAsset.getQuantity(), savedAsset.getCostPerUnit());
            return savedAsset;
        } catch (Exception e) {
            logger.error("Error saving asset with symbol: {}", asset.getSymbol(), e);
            throw e;
        }
    }
    
    public Asset createAsset(String type, String name, String symbol) {
        logger.debug("Creating asset - Type: {}, Name: {}, Symbol: {}", type, name, symbol);
        
        try {
            // Validate input parameters
            if (type == null || type.trim().isEmpty() || 
                name == null || name.trim().isEmpty() || 
                symbol == null || symbol.trim().isEmpty()) {
                logger.error("Invalid asset parameters - Type: {}, Name: {}, Symbol: {}", type, name, symbol);
                throw new InvalidAssetQuantityException("Asset parameters cannot be empty");
            }
            
            // Check if asset already exists
            if (assetRepository.existsBySymbol(symbol)) {
                logger.info("Asset already exists with symbol: {}. Returning existing asset", symbol);
                Optional<Asset> existingAsset = assetRepository.findBySymbol(symbol);
                return existingAsset.get();
            }
            
            Asset asset = new Asset(type, name, symbol);
            Asset savedAsset = assetRepository.save(asset);
            logger.info("Asset created successfully. ID: {}, Type: {}, Name: {}, Symbol: {}", 
                savedAsset.getId(), type, name, symbol);
            return savedAsset;
        } catch (Exception e) {
            logger.error("Error creating asset - Symbol: {}", symbol, e);
            throw e;
        }
    }
    
    public void deleteAsset(Long id) {
        logger.debug("Deleting asset with ID: {}", id);
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid asset ID for deletion: {}", id);
                throw new AssetNotFoundException("Invalid asset ID: " + id, id);
            }
            assetRepository.deleteById(id);
            logger.info("Asset deleted successfully with ID: {}", id);
        } catch (AssetNotFoundException e) {
            logger.error("Error deleting asset with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting asset with ID: {}", id, e);
            throw e;
        }
    }
}

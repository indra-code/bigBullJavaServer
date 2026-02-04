package com.BigBull.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.BigBull.entity.Asset;
import com.BigBull.exception.AssetNotFoundException;
import com.BigBull.exception.InvalidAssetQuantityException;
import com.BigBull.repository.AssetRepository;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset asset;

    @BeforeEach
    void setup() {
        asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        asset.setId(1L);
        asset.setQuantity(10.0);
    }

    @Test
    void testGetAllAssets_ReturnsAllAssets() {
        when(assetRepository.findAll()).thenReturn(List.of(asset));

        List<Asset> assets = assetService.getAllAssets();

        assertEquals(1, assets.size());
        assertEquals("RELIANCE", assets.get(0).getSymbol());
    }

    @Test
    void testGetAllAssets_EmptyDatabase_ReturnsEmptyList() {
        when(assetRepository.findAll()).thenReturn(Collections.emptyList());

        List<Asset> assets = assetService.getAllAssets();

        assertEquals(0, assets.size());
    }

    @Test
    void testGetAssetById_ValidId_ReturnsAsset() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        Optional<Asset> result = assetService.getAssetById(1L);

        assertEquals(true, result.isPresent());
    }

    @Test
    void testGetAssetById_InvalidId_ThrowsAssetNotFoundException() {
        assertThrows(AssetNotFoundException.class, () -> assetService.getAssetById(-1L));
    }

    @Test
    void testGetAssetBySymbol_ValidSymbol_ReturnsAsset() {
        when(assetRepository.findBySymbol("RELIANCE")).thenReturn(Optional.of(asset));

        Optional<Asset> result = assetService.getAssetBySymbol("RELIANCE");

        assertEquals(true, result.isPresent());
    }

    @Test
    void testGetAssetBySymbol_InvalidSymbol_ThrowsAssetNotFoundException() {
        assertThrows(AssetNotFoundException.class, () -> assetService.getAssetBySymbol(""));
    }

    @Test
    void testCreateAsset_NewAsset_CreatesSuccessfully() {
        when(assetRepository.existsBySymbol("RELIANCE")).thenReturn(false);
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        Asset result = assetService.createAsset("STOCK", "Reliance Industries", "RELIANCE");

        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void testCreateAsset_DuplicateSymbol_ReturnsExisting() {
        when(assetRepository.existsBySymbol("RELIANCE")).thenReturn(true);
        when(assetRepository.findBySymbol("RELIANCE")).thenReturn(Optional.of(asset));

        Asset result = assetService.createAsset("STOCK", "Reliance Industries", "RELIANCE");

        assertEquals(1L, result.getId());
    }

    @Test
    void testCreateAsset_NullParameters_ThrowsInvalidAssetQuantityException() {
        assertThrows(InvalidAssetQuantityException.class, () -> assetService.createAsset(null, null, null));
    }

    @Test
    void testSaveAsset_ValidAsset_SavesSuccessfully() {
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        Asset result = assetService.saveAsset(asset);

        assertEquals("RELIANCE", result.getSymbol());
    }

    @Test
    void testDeleteAsset_InvalidId_ThrowsAssetNotFoundException() {
        assertThrows(AssetNotFoundException.class, () -> assetService.deleteAsset(-1L));
    }
}

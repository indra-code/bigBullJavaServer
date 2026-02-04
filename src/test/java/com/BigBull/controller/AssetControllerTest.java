package com.BigBull.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.BigBull.entity.Asset;
import com.BigBull.service.AssetService;

@WebMvcTest(AssetController.class)
public class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @Test
    void testGetAllAssets_Returns200WithList() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAllAssets()).thenReturn(List.of(asset));

        mockMvc.perform(get("/api/assets"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAssetById_ValidId_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(asset));

        mockMvc.perform(get("/api/assets/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAssetById_InvalidId_Returns404() throws Exception {
        when(assetService.getAssetById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAssetBySymbol_ValidSymbol_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAssetBySymbol("RELIANCE")).thenReturn(Optional.of(asset));

        mockMvc.perform(get("/api/assets/symbol/RELIANCE"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAssetBySymbol_InvalidSymbol_Returns404() throws Exception {
        when(assetService.getAssetBySymbol("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assets/symbol/INVALID"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateAsset_ValidAsset_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.saveAsset(asset)).thenReturn(asset);

        mockMvc.perform(post("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"STOCK\",\"name\":\"Reliance Industries\",\"symbol\":\"RELIANCE\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteAsset_ValidId_Returns204() throws Exception {
        mockMvc.perform(delete("/api/assets/1"))
            .andExpect(status().isNoContent());
    }
}

package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
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
        Asset asset1 = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Asset asset2 = new Asset("STOCK", "Tata Motors", "TATAMOTORS");
        when(assetService.getAllAssets()).thenReturn(List.of(asset1, asset2));

        mockMvc.perform(get("/api/assets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].symbol", is("RELIANCE")))
            .andExpect(jsonPath("$[1].symbol", is("TATAMOTORS")));
    }

    @Test
    void testGetAllAssets_EmptyList_Returns200() throws Exception {
        when(assetService.getAllAssets()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/assets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAssetById_ValidId_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(asset));

        mockMvc.perform(get("/api/assets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol", is("RELIANCE")))
            .andExpect(jsonPath("$.name", is("Reliance Industries")))
            .andExpect(jsonPath("$.type", is("STOCK")));

        verify(assetService).getAssetById(1L);
    }

    @Test
    void testGetAssetById_InvalidId_Returns404() throws Exception {
        when(assetService.getAssetById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assets/999"))
            .andExpect(status().isNotFound());

        verify(assetService).getAssetById(999L);
    }

    @Test
    void testGetAssetBySymbol_ValidSymbol_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAssetBySymbol("RELIANCE")).thenReturn(Optional.of(asset));

        mockMvc.perform(get("/api/assets/symbol/RELIANCE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol", is("RELIANCE")))
            .andExpect(jsonPath("$.name", is("Reliance Industries")))
            .andExpect(jsonPath("$.type", is("STOCK")));

        verify(assetService).getAssetBySymbol("RELIANCE");
    }

    @Test
    void testGetAssetBySymbol_InvalidSymbol_Returns404() throws Exception {
        when(assetService.getAssetBySymbol("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/assets/symbol/INVALID"))
            .andExpect(status().isNotFound());

        verify(assetService).getAssetBySymbol("INVALID");
    }

    @Test
    void testCreateAsset_ValidAsset_Returns200() throws Exception {
        Asset savedAsset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.saveAsset(any(Asset.class))).thenReturn(savedAsset);

        mockMvc.perform(post("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"STOCK\",\"name\":\"Reliance Industries\",\"symbol\":\"RELIANCE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol", is("RELIANCE")))
            .andExpect(jsonPath("$.name", is("Reliance Industries")))
            .andExpect(jsonPath("$.type", is("STOCK")));

        verify(assetService).saveAsset(any(Asset.class));
    }

    @Test
    void testDeleteAsset_ValidId_Returns204() throws Exception {
        doNothing().when(assetService).deleteAsset(1L);

        mockMvc.perform(delete("/api/assets/1"))
            .andExpect(status().isNoContent());

        verify(assetService).deleteAsset(1L);
    }
}

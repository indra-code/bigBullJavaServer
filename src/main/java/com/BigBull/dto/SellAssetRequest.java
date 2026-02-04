package com.BigBull.dto;

import lombok.Data;

@Data
public class SellAssetRequest {
    private String username;
    private Long assetId;
    private int units;
    private double pricePerUnit;
}

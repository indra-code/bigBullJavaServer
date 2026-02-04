package com.BigBull.dto;

import lombok.Data;

@Data
public class BuyAssetRequest {
    private String username;
    private String symbol;
    private String name;
    private int units;
    private double pricePerUnit;
}

package com.BigBull.dto;

import lombok.Data;

class AssetCreateRequest {
    private String type;
    private String name;
    private String symbol;
    private Integer quantity;
    private Double costPerUnit;
}

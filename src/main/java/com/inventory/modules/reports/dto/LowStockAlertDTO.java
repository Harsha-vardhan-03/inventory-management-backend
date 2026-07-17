package com.inventory.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertDTO {
    private Long productId;
    private String productName;
    private String sku;
    private String category;
    private Integer currentStock;
    private Integer minimumStockLevel;
    private Integer reorderQuantity;
    private String urgencyLevel;
}
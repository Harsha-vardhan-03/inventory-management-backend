package com.inventory.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDTO {
    private Long productId;
    private String productName;
    private String sku;
    private String category;
    private Integer currentStock;
    private Integer minimumStockLevel;
    private Boolean isLowStock;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String status;
}
package com.inventory.modules.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProductResponse {
    private Long id;
    private String name;
    private String description;
    private String unit;
    private BigDecimal unitPrice;
    private Integer availableQty;
    private Integer minimumStockLevel;  // Add this field
    private Boolean isLowStock; 
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
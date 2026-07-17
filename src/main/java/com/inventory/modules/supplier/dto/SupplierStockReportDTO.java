package com.inventory.modules.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStockReportDTO {
    private Long productId;
    private String productName;
    private String category;
    private Integer currentStock;
    private Integer minimumStockLevel;
    private Boolean isLowStock;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String status;
}
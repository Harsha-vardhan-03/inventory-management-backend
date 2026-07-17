package com.inventory.modules.reports.dto;

import com.inventory.common.enums.StockMovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementSummaryDTO {
    private Long movementId;
    private String productName;
    private String sku;
    private StockMovementType type;
    private Integer quantity;
    private String referenceType;
    private String notes;
    private String performedBy;
    private LocalDateTime createdAt;
}
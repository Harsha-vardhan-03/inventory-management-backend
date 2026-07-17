package com.inventory.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummaryDTO {
    private LocalDate date;
    private Long totalPurchaseOrders;
    private BigDecimal totalPurchaseAmount;
    private Integer totalItemsPurchased;
    private Long approvedOrders;
    private Long pendingOrders;
    private Long rejectedOrders;
    private Long deliveredOrders;
}
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
public class DashboardSummaryDTO {
    private Long totalProducts;
    private Long lowStockCount;
    private Long outOfStockCount;
    private Long pendingPurchaseOrders;
    private Long todaySalesCount;
    private BigDecimal todaySalesRevenue;
    private Integer todayItemsSold;
    private Long currentMonthSalesCount;
    private BigDecimal currentMonthRevenue;
    private Integer currentMonthItemsSold;
    private Long totalCustomers;
    private Long totalSuppliers;
    private BigDecimal totalInventoryValue;
}
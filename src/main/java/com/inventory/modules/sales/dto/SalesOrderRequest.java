package com.inventory.modules.sales.dto;

import com.inventory.common.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class SalesOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private BigDecimal discount = BigDecimal.ZERO;
    
    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
    
    @NotNull(message = "Items are required")
    private List<SaleItemDTO> items;
}
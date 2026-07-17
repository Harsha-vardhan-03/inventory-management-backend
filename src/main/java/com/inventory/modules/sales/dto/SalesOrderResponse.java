package com.inventory.modules.sales.dto;

import com.inventory.common.enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private PaymentMode paymentMode;
    private String status;
    private LocalDateTime createdAt;
    private List<SaleItemDTO> items;
}
/*
 * package com.inventory.modules.purchase.dto;
 * 
 * import com.inventory.common.enums.OrderStatus; import
 * lombok.AllArgsConstructor; import lombok.Builder; import lombok.Data; import
 * lombok.NoArgsConstructor; import java.math.BigDecimal; import
 * java.time.LocalDateTime; import java.util.List;
 * 
 * @Data
 * 
 * @Builder
 * 
 * @NoArgsConstructor
 * 
 * @AllArgsConstructor public class PurchaseRequestResponse { private Long id;
 * private Long companyId; private String companyName; private Long supplierId;
 * private String supplierName; private OrderStatus status; private BigDecimal
 * totalAmount; private String rejectionReason; private String deliveryNotes;
 * private LocalDateTime dispatchedAt; private LocalDateTime deliveredAt;
 * private LocalDateTime createdAt; private LocalDateTime updatedAt; private
 * List<PurchaseRequestItemDTO> items; }
 */








package com.inventory.modules.purchase.dto;

import com.inventory.common.enums.OrderStatus;
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
public class PurchaseRequestResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long supplierId;
    private String supplierName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String rejectionReason;
    private String deliveryNotes;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseRequestItemDTO> items;
}
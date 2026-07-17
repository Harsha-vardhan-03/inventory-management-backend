/*
 * package com.inventory.modules.purchase.dto;
 * 
 * import jakarta.validation.constraints.Min; import
 * jakarta.validation.constraints.NotNull; import lombok.Data; import
 * java.math.BigDecimal;
 * 
 * @Data public class PurchaseRequestItemDTO {
 * 
 * @NotNull(message = "Supplier product ID is required") private Long
 * supplierProductId;
 * 
 * @NotNull(message = "Product name is required") private String productName;
 * 
 * @NotNull(message = "Requested quantity is required")
 * 
 * @Min(value = 1, message = "Quantity must be at least 1") private Integer
 * requestedQty;
 * 
 * private BigDecimal unitPrice; }
 */








package com.inventory.modules.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestItemDTO {
    
    private Long id;
    
    @NotNull(message = "Supplier product ID is required")
    private Long supplierProductId;
    
    @NotNull(message = "Product name is required")
    private String productName;
    
    @NotNull(message = "Requested quantity is required")
    @Min(value = 1, message = "Requested quantity must be at least 1")
    private Integer requestedQty;
    
    private BigDecimal unitPrice;
    
    private Integer receivedQty;  // Add this field
    
    public PurchaseRequestItemDTO(Long supplierProductId, String productName, Integer requestedQty) {
        this.supplierProductId = supplierProductId;
        this.productName = productName;
        this.requestedQty = requestedQty;
        this.receivedQty = 0;
    }
}
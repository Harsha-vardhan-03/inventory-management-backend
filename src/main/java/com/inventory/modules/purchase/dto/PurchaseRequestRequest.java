package com.inventory.modules.purchase.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PurchaseRequestRequest {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    @NotNull(message = "Items are required")
    private List<PurchaseRequestItemDTO> items;
    
    private String deliveryNotes;
}
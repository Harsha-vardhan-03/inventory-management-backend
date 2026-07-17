package com.inventory.modules.supplier.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanySupplierLinkRequest {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
}
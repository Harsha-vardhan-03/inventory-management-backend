package com.inventory.modules.supplier.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplierProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    private String unit;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQty = 0;
    private Integer minimumStockLevel = 5; 
    
    private String category;
}
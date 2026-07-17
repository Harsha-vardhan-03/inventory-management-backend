package com.inventory.modules.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    private String category;
    
    private String unit;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;
    
    @Min(value = 0, message = "Current stock cannot be negative")
    private Integer currentStock = 0;
    
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minimumStockLevel = 0;
    
    private String imageUrl;
}
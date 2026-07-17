package com.inventory.modules.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStaffStatusRequest {
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
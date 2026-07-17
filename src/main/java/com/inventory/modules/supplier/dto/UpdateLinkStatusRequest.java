package com.inventory.modules.supplier.dto;

import com.inventory.common.enums.LinkStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLinkStatusRequest {
    
    @NotNull(message = "Status is required")
    private LinkStatus status;
    
    private String remarks;
}
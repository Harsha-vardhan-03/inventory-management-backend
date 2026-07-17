/*
 * package com.inventory.modules.purchase.dto;
 * 
 * import lombok.Data; import java.util.Map;
 * 
 * @Data public class DeliveryRequest { private Map<Long, Integer>
 * receivedQuantities; // productId -> received quantity private String notes; }
 */




package com.inventory.modules.purchase.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class DeliveryRequest {
    
    @NotNull(message = "Received quantities are required")
    private Map<Long, Integer> receivedQuantities;  // Map<itemId, receivedQty>
    
    private String deliveryNotes;
}
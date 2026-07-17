package com.inventory.modules.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCustomerResponse {

    private Long companyId;
    private String companyName;
    private String email;
    private String phone;
    private LocalDateTime connectedAt;
}
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
public class SupplierResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private String gstNumber;
    private String website;
    private String contactPerson;
    private String contactPersonPhone;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
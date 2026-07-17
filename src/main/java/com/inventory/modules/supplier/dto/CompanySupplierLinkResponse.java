package com.inventory.modules.supplier.dto;

import com.inventory.common.enums.LinkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySupplierLinkResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long supplierId;
    private String supplierName;
    private LinkStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
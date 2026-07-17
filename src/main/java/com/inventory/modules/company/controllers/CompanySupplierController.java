package com.inventory.modules.company.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.supplier.dto.CompanySupplierLinkRequest;
import com.inventory.modules.supplier.dto.CompanySupplierLinkResponse;
import com.inventory.modules.supplier.dto.SupplierProductResponse;
import com.inventory.modules.supplier.entities.Supplier;
import com.inventory.modules.supplier.services.SupplierService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/company/suppliers")
@RequiredArgsConstructor
public class CompanySupplierController {
    
    private final SupplierService supplierService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }
    
    @GetMapping("/{supplierId}/catalog")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierProductResponse>>> getSupplierCatalog(
            @PathVariable Long supplierId) {
        List<SupplierProductResponse> products = supplierService.getSupplierCatalog(supplierId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @PostMapping("/connect")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CompanySupplierLinkResponse>> sendConnectionRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody CompanySupplierLinkRequest request) {
        CompanySupplierLinkResponse link = supplierService.sendConnectionRequest(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(link, "Connection request sent successfully"));
    }
    
    @GetMapping("/connections")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<CompanySupplierLinkResponse>>> getCompanyConnections(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<CompanySupplierLinkResponse> connections = supplierService.getCompanyConnections(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(connections));
    }
}
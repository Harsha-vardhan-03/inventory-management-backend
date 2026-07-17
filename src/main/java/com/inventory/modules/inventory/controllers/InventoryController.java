package com.inventory.modules.inventory.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.inventory.dto.*;
import com.inventory.modules.inventory.services.InventoryService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = inventoryService.addProduct(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product added successfully"));
    }
    
    @PutMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = inventoryService.updateProduct(currentUser.getCompanyId(), productId, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        inventoryService.deleteProduct(currentUser.getCompanyId(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
    
    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getCompanyProducts(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<ProductResponse> products = inventoryService.getCompanyProducts(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/products/low-stock")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<ProductResponse> products = inventoryService.getLowStockProducts(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        ProductResponse product = inventoryService.getProductById(currentUser.getCompanyId(), productId);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @PostMapping("/stock/adjust")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StockMovementResponse>> adjustStock(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody StockMovementRequest request) {
        StockMovementResponse movement = inventoryService.adjustStock(
            currentUser.getCompanyId(), currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(movement, "Stock adjusted successfully"));
    }
    
    @GetMapping("/stock/movements/product/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getProductMovements(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        List<StockMovementResponse> movements = inventoryService.getProductMovements(
            currentUser.getCompanyId(), productId);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @GetMapping("/stock/movements")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getAllMovements(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<StockMovementResponse> movements = inventoryService.getAllMovements(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
}
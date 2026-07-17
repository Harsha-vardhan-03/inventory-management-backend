/*
 * package com.inventory.modules.supplier.controllers;
 * 
 * import com.inventory.common.response.ApiResponse; import
 * com.inventory.modules.purchase.dto.PurchaseRequestResponse; import
 * com.inventory.modules.supplier.dto.*; import
 * com.inventory.modules.supplier.services.SupplierService; import
 * com.inventory.security.UserDetailsImpl; import jakarta.validation.Valid;
 * import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
 * import org.springframework.http.ResponseEntity; import
 * org.springframework.security.access.prepost.PreAuthorize; import
 * org.springframework.security.core.annotation.AuthenticationPrincipal; import
 * org.springframework.web.bind.annotation.*; import java.util.List;
 * 
 * @RestController
 * 
 * @RequestMapping("/api/supplier")
 * 
 * @RequiredArgsConstructor
 * 
 * @Slf4j public class SupplierController {
 * 
 * private final SupplierService supplierService;
 * 
 * // Get supplier profile
 * 
 * @GetMapping("/profile")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<SupplierResponse>> getSupplierProfile(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) {
 * log.info("Getting supplier profile for user: {}", currentUser.getId());
 * SupplierResponse supplier =
 * supplierService.getSupplierByUserId(currentUser.getId()); return
 * ResponseEntity.ok(ApiResponse.success(supplier)); }
 * 
 * // Update supplier profile
 * 
 * @PutMapping("/profile")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<SupplierResponse>> updateSupplierProfile(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @Valid @RequestBody SupplierRequest request) {
 * log.info("Updating supplier profile for user: {}", currentUser.getId());
 * SupplierResponse supplier =
 * supplierService.updateSupplier(currentUser.getId(), request); return
 * ResponseEntity.ok(ApiResponse.success(supplier,
 * "Profile updated successfully")); }
 * 
 * // Product Management
 * 
 * @PostMapping("/products")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<SupplierProductResponse>> addProduct(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @Valid @RequestBody SupplierProductRequest request) {
 * log.info("Adding product for supplier: {}", currentUser.getId()); Long
 * supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * SupplierProductResponse product = supplierService.addProduct(supplierId,
 * request); return ResponseEntity.ok(ApiResponse.success(product,
 * "Product added successfully")); }
 * 
 * @PutMapping("/products/{productId}")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<SupplierProductResponse>> updateProduct(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long productId,
 * 
 * @Valid @RequestBody SupplierProductRequest request) {
 * log.info("Updating product {} for supplier: {}", productId,
 * currentUser.getId()); Long supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * SupplierProductResponse product = supplierService.updateProduct(supplierId,
 * productId, request); return ResponseEntity.ok(ApiResponse.success(product,
 * "Product updated successfully")); }
 * 
 * @DeleteMapping("/products/{productId}")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public ResponseEntity<ApiResponse<Void>>
 * deleteProduct(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long productId) {
 * log.info("Deleting product {} for supplier: {}", productId,
 * currentUser.getId()); Long supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * supplierService.deleteProduct(supplierId, productId); return
 * ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
 * }
 * 
 * @GetMapping("/products")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<List<SupplierProductResponse>>> getMyProducts(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) {
 * log.info("Getting all products for supplier: {}", currentUser.getId()); Long
 * supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * List<SupplierProductResponse> products =
 * supplierService.getSupplierProducts(supplierId); return
 * ResponseEntity.ok(ApiResponse.success(products)); }
 * 
 * @GetMapping("/products/{productId}")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<SupplierProductResponse>> getProductById(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long productId) {
 * log.info("Getting product {} for supplier: {}", productId,
 * currentUser.getId()); Long supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * SupplierProductResponse product = supplierService.getProductById(supplierId,
 * productId); return ResponseEntity.ok(ApiResponse.success(product)); }
 * 
 * // Connection Management for Supplier
 * 
 * @GetMapping("/connections")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<List<CompanySupplierLinkResponse>>>
 * getMyConnections(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) {
 * log.info("Getting connections for supplier: {}", currentUser.getId()); Long
 * supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * List<CompanySupplierLinkResponse> connections =
 * supplierService.getSupplierConnections(supplierId); return
 * ResponseEntity.ok(ApiResponse.success(connections)); }
 * 
 * @PatchMapping("/connections/{linkId}/status")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<CompanySupplierLinkResponse>>
 * updateConnectionStatus(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long linkId,
 * 
 * @Valid @RequestBody UpdateLinkStatusRequest request) {
 * log.info("Updating connection status for link: {} for supplier: {}", linkId,
 * currentUser.getId()); Long supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * CompanySupplierLinkResponse link =
 * supplierService.updateConnectionStatus(supplierId, linkId, request); return
 * ResponseEntity.ok(ApiResponse.success(link, "Connection status updated")); }
 * 
 * // Get incoming purchase requests
 * 
 * @GetMapping("/purchase-requests")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>>
 * getIncomingPurchaseRequests(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) {
 * log.info("Getting incoming purchase requests for supplier: {}",
 * currentUser.getId()); Long supplierId =
 * supplierService.getSupplierByUserId(currentUser.getId()).getId();
 * List<PurchaseRequestResponse> requests =
 * supplierService.getIncomingPurchaseRequests(supplierId); return
 * ResponseEntity.ok(ApiResponse.success(requests)); } }
 */








package com.inventory.modules.supplier.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.purchase.dto.PurchaseRequestResponse;
import com.inventory.modules.supplier.dto.*;
import com.inventory.modules.supplier.entities.SupplierProduct;
import com.inventory.modules.supplier.services.SupplierService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {
    
    private final SupplierService supplierService;
    
    // Get supplier profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting supplier profile for user: {}", currentUser.getId());
        SupplierResponse supplier = supplierService.getSupplierByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(supplier));
    }
    
    // Update supplier profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplierProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody SupplierRequest request) {
        log.info("Updating supplier profile for user: {}", currentUser.getId());
        SupplierResponse supplier = supplierService.updateSupplier(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(supplier, "Profile updated successfully"));
    }
    
    // ========== PRODUCT MANAGEMENT ==========
    
    @PostMapping("/products")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierProductResponse>> addProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody SupplierProductRequest request) {
        log.info("Adding product for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        SupplierProductResponse product = supplierService.addProduct(supplierId, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product added successfully"));
    }
    
    @PutMapping("/products/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierProductResponse>> updateProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId,
            @Valid @RequestBody SupplierProductRequest request) {
        log.info("Updating product {} for supplier: {}", productId, currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        SupplierProductResponse product = supplierService.updateProduct(supplierId, productId, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        log.info("Deleting product {} for supplier: {}", productId, currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        supplierService.deleteProduct(supplierId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
    
    @GetMapping("/products")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierProductResponse>>> getMyProducts(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting all products for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<SupplierProductResponse> products = supplierService.getSupplierProducts(supplierId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/products/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierProductResponse>> getProductById(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        log.info("Getting product {} for supplier: {}", productId, currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        SupplierProductResponse product = supplierService.getProductById(supplierId, productId);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @GetMapping("/products/low-stock")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierProductResponse>>> getLowStockProducts(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting low stock products for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<SupplierProduct> lowStockProducts = supplierService.getLowStockProducts(supplierId);
        List<SupplierProductResponse> responses = lowStockProducts.stream()
            .map(supplierService::mapToProductResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    @PatchMapping("/products/{productId}/min-stock")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierProductResponse>> updateMinimumStockLevel(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId,
            @RequestParam Integer minimumStockLevel) {
        log.info("Updating minimum stock level for product {}: {}", productId, minimumStockLevel);
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        SupplierProductResponse response = supplierService.updateMinimumStockLevel(supplierId, productId, minimumStockLevel);
        return ResponseEntity.ok(ApiResponse.success(response, "Minimum stock level updated"));
    }
    
    // ========== STOCK MANAGEMENT ==========
    
    @PostMapping("/stock/adjust")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<SupplierStockMovementResponse>> adjustStock(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody SupplierStockAdjustmentRequest request) {
        log.info("Adjusting stock for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        SupplierStockMovementResponse response = supplierService.adjustStock(supplierId, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Stock adjusted successfully"));
    }
    
    @GetMapping("/stock/movements/product/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierStockMovementResponse>>> getProductStockMovements(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long productId) {
        log.info("Getting stock movements for product: {}", productId);
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<SupplierStockMovementResponse> movements = supplierService.getProductStockMovements(supplierId, productId);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @GetMapping("/stock/movements")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierStockMovementResponse>>> getAllStockMovements(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting all stock movements for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<SupplierStockMovementResponse> movements = supplierService.getAllStockMovements(supplierId);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @GetMapping("/stock/report")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierStockReportDTO>>> getSupplierStockReport(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting stock report for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<SupplierStockReportDTO> report = supplierService.getSupplierStockReport(supplierId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    // ========== CONNECTION MANAGEMENT ==========
    
    @GetMapping("/connections")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<CompanySupplierLinkResponse>>> getMyConnections(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting connections for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<CompanySupplierLinkResponse> connections = supplierService.getSupplierConnections(supplierId);
        return ResponseEntity.ok(ApiResponse.success(connections));
    }
    
    @PatchMapping("/connections/{linkId}/status")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<CompanySupplierLinkResponse>> updateConnectionStatus(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long linkId,
            @Valid @RequestBody UpdateLinkStatusRequest request) {
        log.info("Updating connection status for link: {} for supplier: {}", linkId, currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        CompanySupplierLinkResponse link = supplierService.updateConnectionStatus(supplierId, linkId, request);
        return ResponseEntity.ok(ApiResponse.success(link, "Connection status updated"));
    }
    
    // ========== PURCHASE REQUESTS ==========
    
    @GetMapping("/purchase-requests")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>> getIncomingPurchaseRequests(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting incoming purchase requests for supplier: {}", currentUser.getId());
        Long supplierId = supplierService.getSupplierByUserId(currentUser.getId()).getId();
        List<PurchaseRequestResponse> requests = supplierService.getIncomingPurchaseRequests(supplierId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    @GetMapping("/customers")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<SupplierCustomerResponse>>> getCustomers(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        Long supplierId =
                supplierService.getSupplierByUserId(currentUser.getId()).getId();

        List<SupplierCustomerResponse> customers =
                supplierService.getCustomers(supplierId);

        return ResponseEntity.ok(
                ApiResponse.success(customers)
        );
    }
}
/*
 * package com.inventory.modules.purchase.controllers;
 * 
 * import com.inventory.common.response.ApiResponse; import
 * com.inventory.modules.purchase.dto.*; import
 * com.inventory.modules.purchase.services.PurchaseRequestService; import
 * com.inventory.security.UserDetailsImpl; import jakarta.validation.Valid;
 * import lombok.RequiredArgsConstructor; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.security.access.prepost.PreAuthorize; import
 * org.springframework.security.core.annotation.AuthenticationPrincipal; import
 * org.springframework.web.bind.annotation.*; import java.util.List;
 * 
 * @RestController
 * 
 * @RequestMapping("/api/purchase-requests")
 * 
 * @RequiredArgsConstructor public class PurchaseRequestController {
 * 
 * private final PurchaseRequestService purchaseRequestService;
 * 
 * @PostMapping
 * 
 * @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')") public
 * ResponseEntity<ApiResponse<PurchaseRequestResponse>> createPurchaseRequest(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @Valid @RequestBody PurchaseRequestRequest request) { PurchaseRequestResponse
 * response = purchaseRequestService.createPurchaseRequest(
 * currentUser.getCompanyId(), currentUser.getId(), request); return
 * ResponseEntity.ok(ApiResponse.success(response,
 * "Purchase request created successfully")); }
 * 
 * @GetMapping("/company")
 * 
 * @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')") public
 * ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>>
 * getCompanyPurchaseRequests(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) {
 * List<PurchaseRequestResponse> requests =
 * purchaseRequestService.getCompanyPurchaseRequests(currentUser.getCompanyId())
 * ; return ResponseEntity.ok(ApiResponse.success(requests)); }
 * 
 * @GetMapping("/supplier")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>>
 * getSupplierPurchaseRequests(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser) { // Get supplier ID
 * from user - you'll need to implement this mapping Long supplierId =
 * getSupplierIdFromUser(currentUser.getId()); List<PurchaseRequestResponse>
 * requests = purchaseRequestService.getSupplierPurchaseRequests(supplierId);
 * return ResponseEntity.ok(ApiResponse.success(requests)); }
 * 
 * @PatchMapping("/{id}/approve")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<PurchaseRequestResponse>> approvePurchaseRequest(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long id) { Long supplierId =
 * getSupplierIdFromUser(currentUser.getId()); PurchaseRequestResponse response
 * = purchaseRequestService.approvePurchaseRequest(id, supplierId); return
 * ResponseEntity.ok(ApiResponse.success(response,
 * "Purchase request approved")); }
 * 
 * @PatchMapping("/{id}/reject")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<PurchaseRequestResponse>> rejectPurchaseRequest(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long id,
 * 
 * @RequestBody ApproveRejectRequest request) { Long supplierId =
 * getSupplierIdFromUser(currentUser.getId()); PurchaseRequestResponse response
 * = purchaseRequestService.rejectPurchaseRequest(id, supplierId, request);
 * return ResponseEntity.ok(ApiResponse.success(response,
 * "Purchase request rejected")); }
 * 
 * @PatchMapping("/{id}/dispatch")
 * 
 * @PreAuthorize("hasRole('SUPPLIER')") public
 * ResponseEntity<ApiResponse<PurchaseRequestResponse>> dispatchPurchaseRequest(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long id) { Long supplierId =
 * getSupplierIdFromUser(currentUser.getId()); PurchaseRequestResponse response
 * = purchaseRequestService.dispatchPurchaseRequest(id, supplierId); return
 * ResponseEntity.ok(ApiResponse.success(response,
 * "Purchase request dispatched")); }
 * 
 * @PatchMapping("/{id}/deliver")
 * 
 * @PreAuthorize("hasAnyRole('MANAGER', 'WORKER')") public
 * ResponseEntity<ApiResponse<PurchaseRequestResponse>> deliverPurchaseRequest(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long id,
 * 
 * @Valid @RequestBody DeliveryRequest request) { PurchaseRequestResponse
 * response = purchaseRequestService.deliverPurchaseRequest( id,
 * currentUser.getCompanyId(), currentUser.getId(), request); return
 * ResponseEntity.ok(ApiResponse.success(response,
 * "Purchase request delivered and stock updated")); }
 * 
 * @GetMapping("/{id}")
 * 
 * @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER', 'SUPPLIER')"
 * ) public ResponseEntity<ApiResponse<PurchaseRequestResponse>>
 * getPurchaseRequestById(
 * 
 * @AuthenticationPrincipal UserDetailsImpl currentUser,
 * 
 * @PathVariable Long id) { PurchaseRequestResponse response =
 * purchaseRequestService.getPurchaseRequestById(id,
 * currentUser.getCompanyId()); return
 * ResponseEntity.ok(ApiResponse.success(response)); }
 * 
 * // Helper method - you should implement this properly private Long
 * getSupplierIdFromUser(Long userId) { // Query supplier repository to get
 * supplier ID from user ID // For now, return a placeholder return 1L; } }
 */






package com.inventory.modules.purchase.controllers;

import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.common.response.ApiResponse;
import com.inventory.modules.purchase.dto.*;
import com.inventory.modules.purchase.services.PurchaseRequestService;
import com.inventory.modules.supplier.repositories.SupplierRepository;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
@Slf4j
public class PurchaseRequestController {
    
    private final PurchaseRequestService purchaseRequestService;
    private final SupplierRepository supplierRepository;  // ✅ Add this dependency
    
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> createPurchaseRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody PurchaseRequestRequest request) {
        log.info("Creating purchase request for company: {}", currentUser.getCompanyId());
        PurchaseRequestResponse response = purchaseRequestService.createPurchaseRequest(
            currentUser.getCompanyId(), currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase request created successfully"));
    }
    
    @GetMapping("/company")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>> getCompanyPurchaseRequests(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting purchase requests for company: {}", currentUser.getCompanyId());
        List<PurchaseRequestResponse> requests = purchaseRequestService.getCompanyPurchaseRequests(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    @GetMapping("/supplier")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<List<PurchaseRequestResponse>>> getSupplierPurchaseRequests(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long supplierId = getSupplierIdFromUser(currentUser.getId());
        log.info("Getting purchase requests for supplier ID: {}", supplierId);
        List<PurchaseRequestResponse> requests = purchaseRequestService.getSupplierPurchaseRequests(supplierId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> approvePurchaseRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long id) {
        Long supplierId = getSupplierIdFromUser(currentUser.getId());
        log.info("Supplier {} approving purchase request: {}", supplierId, id);
        PurchaseRequestResponse response = purchaseRequestService.approvePurchaseRequest(id, supplierId);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase request approved"));
    }
    
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> rejectPurchaseRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long id,
            @RequestBody ApproveRejectRequest request) {
        Long supplierId = getSupplierIdFromUser(currentUser.getId());
        log.info("Supplier {} rejecting purchase request: {}", supplierId, id);
        PurchaseRequestResponse response = purchaseRequestService.rejectPurchaseRequest(id, supplierId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase request rejected"));
    }
    
    @PatchMapping("/{id}/dispatch")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> dispatchPurchaseRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long id) {
        Long supplierId = getSupplierIdFromUser(currentUser.getId());
        log.info("Supplier {} dispatching purchase request: {}", supplierId, id);
        PurchaseRequestResponse response = purchaseRequestService.dispatchPurchaseRequest(id, supplierId);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase request dispatched"));
    }
    
    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('MANAGER', 'WORKER', 'OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> deliverPurchaseRequest(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long id,
            @Valid @RequestBody DeliveryRequest request) {
        log.info("User {} delivering purchase request: {}", currentUser.getId(), id);
        PurchaseRequestResponse response = purchaseRequestService.deliverPurchaseRequest(
            id, currentUser.getCompanyId(), currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase request delivered and stock updated"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER', 'SUPPLIER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> getPurchaseRequestById(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long id) {
        log.info("Getting purchase request by ID: {}", id);
        PurchaseRequestResponse response = purchaseRequestService.getPurchaseRequestById(id, currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * ✅ Helper method to get supplier ID from user ID using SupplierRepository
     */
    private Long getSupplierIdFromUser(Long userId) {
        log.debug("Getting supplier ID for user ID: {}", userId);
        
        return supplierRepository.findByUserId(userId)
            .map(supplier -> {
                log.debug("Found supplier ID: {} for user ID: {}", supplier.getId(), userId);
                return supplier.getId();
            })
            .orElseThrow(() -> {
                log.error("Supplier not found for user ID: {}", userId);
                return new ResourceNotFoundException("Supplier not found for user with ID: " + userId);
            });
    }
}
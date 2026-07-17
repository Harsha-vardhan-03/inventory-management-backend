package com.inventory.modules.sales.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.sales.dto.*;
import com.inventory.modules.sales.services.SalesService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    
    private final SalesService salesService;
    
    // Customer Management
    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> addCustomer(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse customer = salesService.addCustomer(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(customer, "Customer added successfully"));
    }
    
    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getCompanyCustomers(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<CustomerResponse> customers = salesService.getCompanyCustomers(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    // Sales Orders
    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> createSalesOrder(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody SalesOrderRequest request) {
        SalesOrderResponse order = salesService.createSalesOrder(
            currentUser.getCompanyId(), currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(order, "Sale completed successfully"));
    }
    
    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getCompanyOrders(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<SalesOrderResponse> orders = salesService.getCompanyOrders(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getOrderById(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long orderId) {
        SalesOrderResponse order = salesService.getOrderById(currentUser.getCompanyId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @GetMapping("/orders/{orderId}/invoice")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getInvoice(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long orderId) {
        SalesOrderResponse order = salesService.getOrderById(currentUser.getCompanyId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Invoice generated successfully"));
    }
}
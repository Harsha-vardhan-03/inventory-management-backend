package com.inventory.modules.reports.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.reports.dto.*;
import com.inventory.modules.reports.services.ReportsService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    private final ReportsService reportsService;
    
    @GetMapping("/inventory-summary")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<InventorySummaryDTO>>> getInventorySummary(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<InventorySummaryDTO> summary = reportsService.getInventorySummary(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
    
    @GetMapping("/stock-movements")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<StockMovementSummaryDTO>>> getStockMovements(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<StockMovementSummaryDTO> movements = reportsService.getStockMovements(
            currentUser.getCompanyId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @GetMapping("/sales-summary")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<SalesSummaryDTO>>> getSalesSummary(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SalesSummaryDTO> summary = reportsService.getSalesSummary(
            currentUser.getCompanyId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
    
    @GetMapping("/purchase-summary")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<PurchaseSummaryDTO>>> getPurchaseSummary(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PurchaseSummaryDTO> summary = reportsService.getPurchaseSummary(
            currentUser.getCompanyId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
    
    @GetMapping("/low-stock-alerts")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<LowStockAlertDTO>>> getLowStockAlerts(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<LowStockAlertDTO> alerts = reportsService.getLowStockAlerts(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }
    
    @GetMapping("/top-products")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProducts(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProductDTO> products = reportsService.getTopProducts(currentUser.getCompanyId(), limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        DashboardSummaryDTO summary = reportsService.getDashboardSummary(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
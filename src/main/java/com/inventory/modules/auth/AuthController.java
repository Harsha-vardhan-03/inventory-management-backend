package com.inventory.modules.auth;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register/owner")
    public ResponseEntity<ApiResponse<LoginResponse>> registerOwner(
            @Valid @RequestBody RegisterOwnerRequest request) {
        log.info("REST request to register owner: {}", request.getEmail());
        LoginResponse response = authService.registerOwner(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Company registered successfully"));
    }
    
    @PostMapping("/register/supplier")
    public ResponseEntity<ApiResponse<LoginResponse>> registerSupplier(
            @Valid @RequestBody RegisterSupplierRequest request) {
        log.info("REST request to register supplier: {}", request.getEmail());
        LoginResponse response = authService.registerSupplier(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier registered successfully"));
    }
    
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LoginResponse>> registerAdmin(
            @Valid @RequestBody RegisterAdminRequest request) {
        log.info("REST request to register admin: {} for company: {}", request.getEmail(), request.getCompanyId());
        LoginResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Admin registered successfully"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("REST request to login: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String refreshToken) {
        log.info("REST request to refresh token");
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("REST request to logout");
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }
    
    @PostMapping("/set-password")
    public ResponseEntity<ApiResponse<Void>> setPassword(
            @Valid @RequestBody SetPasswordRequest request,
            @RequestHeader("Authorization") String token) {
        log.info("REST request to set new password");
        authService.setPassword(request, token);
        return ResponseEntity.ok(ApiResponse.success(null, "Password updated successfully"));
    }
    
    
}
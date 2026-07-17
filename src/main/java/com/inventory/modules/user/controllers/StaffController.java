package com.inventory.modules.user.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.user.dto.StaffInviteRequest;
import com.inventory.modules.user.dto.StaffResponse;
import com.inventory.modules.user.dto.UpdateStaffStatusRequest;
import com.inventory.modules.user.services.StaffService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
public class StaffController {
    
    private final StaffService staffService;
    
    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> inviteStaff(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody StaffInviteRequest request) {
        StaffResponse staff = staffService.inviteStaff(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(staff, "Staff invited successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getCompanyStaff(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<StaffResponse> staff = staffService.getCompanyStaff(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(staff));
    }
    
    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaffById(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long staffId) {
        StaffResponse staff = staffService.getStaffById(currentUser.getCompanyId(), staffId);
        return ResponseEntity.ok(ApiResponse.success(staff));
    }
    
    @PatchMapping("/{staffId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaffStatus(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long staffId,
            @Valid @RequestBody UpdateStaffStatusRequest request) {
        StaffResponse staff = staffService.updateStaffStatus(currentUser.getCompanyId(), staffId, request);
        String message = request.getIsActive() ? "Staff activated successfully" : "Staff deactivated successfully";
        return ResponseEntity.ok(ApiResponse.success(staff, message));
    }
}
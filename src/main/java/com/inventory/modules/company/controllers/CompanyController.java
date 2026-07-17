package com.inventory.modules.company.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.company.dto.CompanyRequest;
import com.inventory.modules.company.dto.CompanyResponse;
import com.inventory.modules.company.dto.DepartmentRequest;
import com.inventory.modules.company.dto.DepartmentResponse;
import com.inventory.modules.company.services.CompanyService;
import com.inventory.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
    
    private final CompanyService companyService;
    
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        CompanyResponse company = companyService.getCompany(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(company));
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompanyProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody CompanyRequest request) {
        CompanyResponse company = companyService.updateCompany(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(company, "Company profile updated successfully"));
    }
    
    @PostMapping("/departments")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> addDepartment(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department = companyService.addDepartment(currentUser.getCompanyId(), request);
        return ResponseEntity.ok(ApiResponse.success(department, "Department added successfully"));
    }
    
    @PutMapping("/departments/{departmentId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long departmentId,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse department = companyService.updateDepartment(
            currentUser.getCompanyId(), departmentId, request);
        return ResponseEntity.ok(ApiResponse.success(department, "Department updated successfully"));
    }
    
    @DeleteMapping("/departments/{departmentId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long departmentId) {
        companyService.deleteDepartment(currentUser.getCompanyId(), departmentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Department deleted successfully"));
    }
    
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'WORKER')")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<DepartmentResponse> departments = companyService.getDepartments(currentUser.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(departments));
    }
}
package com.inventory.modules.company.services;

import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.modules.company.dto.CompanyRequest;
import com.inventory.modules.company.dto.CompanyResponse;
import com.inventory.modules.company.dto.DepartmentRequest;
import com.inventory.modules.company.dto.DepartmentResponse;
import com.inventory.modules.company.entities.Company;
import com.inventory.modules.company.entities.Department;
import com.inventory.modules.company.repositories.CompanyRepository;
import com.inventory.modules.company.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public CompanyResponse updateCompany(Long companyId, CompanyRequest request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        if (!company.getName().equals(request.getName()) && 
            companyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Company name already exists");
        }
        
        company.setName(request.getName());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setAddress(request.getAddress());
        company.setGstNumber(request.getGstNumber());
        company.setLogoUrl(request.getLogoUrl());
        
        company = companyRepository.save(company);
        return mapToResponse(company);
    }
    
    public CompanyResponse getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return mapToResponse(company);
    }
    
    @Transactional
    public DepartmentResponse addDepartment(Long companyId, DepartmentRequest request) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found");
        }
        
        if (departmentRepository.existsByCompanyIdAndName(companyId, request.getName())) {
            throw new DuplicateResourceException("Department with this name already exists");
        }
        
        Department department = Department.builder()
            .companyId(companyId)
            .name(request.getName())
            .description(request.getDescription())
            .build();
        
        department = departmentRepository.save(department);
        return mapToDepartmentResponse(department);
    }
    
    @Transactional
    public DepartmentResponse updateDepartment(Long companyId, Long departmentId, DepartmentRequest request) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        if (!department.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Department not found for this company");
        }
        
        if (!department.getName().equals(request.getName()) &&
            departmentRepository.existsByCompanyIdAndName(companyId, request.getName())) {
            throw new DuplicateResourceException("Department with this name already exists");
        }
        
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        
        department = departmentRepository.save(department);
        return mapToDepartmentResponse(department);
    }
    
    @Transactional
    public void deleteDepartment(Long companyId, Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        if (!department.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Department not found for this company");
        }
        
        departmentRepository.delete(department);
    }
    
    public List<DepartmentResponse> getDepartments(Long companyId) {
        return departmentRepository.findByCompanyId(companyId)
            .stream()
            .map(this::mapToDepartmentResponse)
            .collect(Collectors.toList());
    }
    
    private CompanyResponse mapToResponse(Company company) {
        return CompanyResponse.builder()
            .id(company.getId())
            .name(company.getName())
            .email(company.getEmail())
            .phone(company.getPhone())
            .address(company.getAddress())
            .gstNumber(company.getGstNumber())
            .logoUrl(company.getLogoUrl())
            .createdAt(company.getCreatedAt())
            .updatedAt(company.getUpdatedAt())
            .build();
    }
    
    private DepartmentResponse mapToDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
            .id(department.getId())
            .name(department.getName())
            .description(department.getDescription())
            .createdAt(department.getCreatedAt())
            .build();
    }
}
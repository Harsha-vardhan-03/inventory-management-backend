package com.inventory.modules.user.services;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.common.enums.Role;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.config.EmailService;
import com.inventory.modules.company.entities.Department;
import com.inventory.modules.company.repositories.DepartmentRepository;
import com.inventory.modules.user.dto.StaffInviteRequest;
import com.inventory.modules.user.dto.StaffResponse;
import com.inventory.modules.user.dto.UpdateStaffStatusRequest;
import com.inventory.modules.user.entities.User;
import com.inventory.modules.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {
    
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
	/*
	 * @Transactional public StaffResponse inviteStaff(Long companyId,
	 * StaffInviteRequest request) { // Validate role if (request.getRole() !=
	 * Role.MANAGER && request.getRole() != Role.WORKER) { throw new
	 * IllegalArgumentException("Invalid role for staff invitation. Only MANAGER and WORKER can be invited."
	 * ); }
	 * 
	 * // Check if email exists if
	 * (userRepository.existsByEmail(request.getEmail())) { throw new
	 * DuplicateResourceException("Email already registered"); }
	 * 
	 * // Validate department if provided Long departmentId = null; String
	 * departmentName = null;
	 * 
	 * if (request.getDepartmentId() != null) { Department department =
	 * departmentRepository.findById(request.getDepartmentId()) .orElseThrow(() ->
	 * new ResourceNotFoundException("Department not found"));
	 * 
	 * if (!department.getCompanyId().equals(companyId)) { throw new
	 * ResourceNotFoundException("Department not found for this company"); }
	 * departmentId = department.getId(); departmentName = department.getName(); }
	 * 
	 * // Generate temporary password String temporaryPassword =
	 * generateTemporaryPassword();
	 * 
	 * // Create user User user = User.builder() .name(request.getName())
	 * .email(request.getEmail())
	 * .password(passwordEncoder.encode(temporaryPassword)) .role(request.getRole())
	 * .companyId(companyId) .departmentId(departmentId) .isActive(true)
	 * .isPasswordResetRequired(true) .build();
	 * 
	 * user = userRepository.save(user);
	 * 
	 * // Send invitation email emailService.sendInvitationEmail(request.getEmail(),
	 * request.getName(), temporaryPassword);
	 * 
	 * // Build response return StaffResponse.builder() .id(user.getId())
	 * .name(user.getName()) .email(user.getEmail()) .role(user.getRole())
	 * .departmentId(departmentId) .departmentName(departmentName)
	 * .isActive(user.getIsActive()) .createdAt(user.getCreatedAt()) .build(); }
	 */
    @Transactional
    public StaffResponse inviteStaff(Long companyId, StaffInviteRequest request) {
        // Validate role
        if (request.getRole() != Role.MANAGER && request.getRole() != Role.WORKER) {
            throw new IllegalArgumentException("Invalid role for staff invitation. Only MANAGER and WORKER can be invited.");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        // Validate department if provided
        Long departmentId = null;
        String departmentName = null;
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            
            if (!department.getCompanyId().equals(companyId)) {
                throw new ResourceNotFoundException("Department not found for this company");
            }
            departmentId = department.getId();
            departmentName = department.getName();
        }
        
        // Generate temporary password
        String temporaryPassword = generateTemporaryPassword();
        
        // Create user
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(temporaryPassword))
            .role(request.getRole())
            .companyId(companyId)
            .departmentId(departmentId)
            .isActive(true)
            .isPasswordResetRequired(true)
            .build();
        
        user = userRepository.save(user);
        
        // Send real email invitation
        try {
            emailService.sendInvitationEmail(request.getEmail(), request.getName(), temporaryPassword);
            log.info("Invitation email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send invitation email: {}", e.getMessage());
            // Still continue, user is created
        }
        
        return buildStaffResponse(user);
    }
    @Transactional
    public StaffResponse updateStaffStatus(Long companyId, Long staffId, UpdateStaffStatusRequest request) {
        User staff = userRepository.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        
        if (!staff.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Staff not found for this company");
        }
        
        if (staff.getRole() == Role.OWNER || staff.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot deactivate owner or admin");
        }
        
        staff.setIsActive(request.getIsActive());
        staff = userRepository.save(staff);
        
        return buildStaffResponse(staff);
    }
    
    public List<StaffResponse> getCompanyStaff(Long companyId) {
        return userRepository.findByCompanyId(companyId)
            .stream()
            .filter(user -> user.getRole() != Role.SUPPLIER && user.getRole() != Role.SUPER_ADMIN)
            .map(this::buildStaffResponse)
            .collect(Collectors.toList());
    }
    
    public StaffResponse getStaffById(Long companyId, Long staffId) {
        User staff = userRepository.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        
        if (!staff.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Staff not found for this company");
        }
        
        return buildStaffResponse(staff);
    }
    
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8) + "@123";
    }
    
    private StaffResponse buildStaffResponse(User user) {
        String departmentName = null;
        if (user.getDepartmentId() != null) {
            // Fix: Use Optional and get directly instead of modifying inside lambda
            Optional<Department> departmentOpt = departmentRepository.findById(user.getDepartmentId());
            if (departmentOpt.isPresent()) {
                departmentName = departmentOpt.get().getName();
            }
        }
        
        return StaffResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .departmentId(user.getDepartmentId())
            .departmentName(departmentName)
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
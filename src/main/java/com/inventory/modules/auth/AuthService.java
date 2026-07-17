/*
 * package com.inventory.modules.auth;
 * 
 * import com.inventory.common.enums.Role; import
 * com.inventory.common.exception.DuplicateResourceException; import
 * com.inventory.common.exception.ResourceNotFoundException; import
 * com.inventory.modules.auth.dto.*; import
 * com.inventory.modules.company.entities.Company; import
 * com.inventory.modules.company.repositories.CompanyRepository; import
 * com.inventory.modules.supplier.entities.Supplier; import
 * com.inventory.modules.supplier.repositories.SupplierRepository; import
 * com.inventory.modules.user.entities.RefreshToken; import
 * com.inventory.modules.user.entities.User; import
 * com.inventory.modules.user.repositories.RefreshTokenRepository; import
 * com.inventory.modules.user.repositories.UserRepository; import
 * com.inventory.security.JwtUtil; import
 * com.inventory.security.UserDetailsImpl; import
 * lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import
 * org.springframework.security.authentication.AuthenticationManager; import
 * org.springframework.security.authentication.
 * UsernamePasswordAuthenticationToken; import
 * org.springframework.security.core.Authentication; import
 * org.springframework.security.core.context.SecurityContextHolder; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional; import
 * java.time.LocalDateTime;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor
 * 
 * @Slf4j public class AuthService {
 * 
 * private final UserRepository userRepository; private final CompanyRepository
 * companyRepository; private final SupplierRepository supplierRepository;
 * private final RefreshTokenRepository refreshTokenRepository; private final
 * PasswordEncoder passwordEncoder; private final AuthenticationManager
 * authenticationManager; private final JwtUtil jwtUtil;
 * 
 * @Transactional public LoginResponse registerOwner(RegisterOwnerRequest
 * request) { log.info("Registering new owner with email: {}",
 * request.getEmail());
 * 
 * if (userRepository.existsByEmail(request.getEmail())) { throw new
 * DuplicateResourceException("Email already registered"); }
 * 
 * // Create company Company company = Company.builder()
 * .name(request.getCompanyName()) .email(request.getEmail())
 * .phone(request.getPhone()) .address(request.getAddress()) .build(); company =
 * companyRepository.save(company); log.info("Company created with ID: {}",
 * company.getId());
 * 
 * // Create user User user = User.builder() .name(request.getOwnerName())
 * .email(request.getEmail())
 * .password(passwordEncoder.encode(request.getPassword())) .role(Role.OWNER)
 * .companyId(company.getId()) .isActive(true) .isPasswordResetRequired(false)
 * .build(); user = userRepository.save(user);
 * log.info("Owner user created with ID: {}", user.getId());
 * 
 * // Generate tokens UserDetailsImpl userDetails = UserDetailsImpl.build(user);
 * String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(),
 * user.getRole().name(), user.getCompanyId(), user.getName()); String
 * refreshToken = generateRefreshToken(userDetails, user.getId());
 * 
 * return buildLoginResponse(user, accessToken, refreshToken); }
 * 
 * @Transactional public LoginResponse registerSupplier(RegisterSupplierRequest
 * request) { log.info("Registering new supplier with email: {}",
 * request.getEmail());
 * 
 * if (userRepository.existsByEmail(request.getEmail())) { throw new
 * DuplicateResourceException("Email already registered"); }
 * 
 * // Create user User user = User.builder() .name(request.getSupplierName())
 * .email(request.getEmail())
 * .password(passwordEncoder.encode(request.getPassword())) .role(Role.SUPPLIER)
 * .companyId(null) .isActive(true) .isPasswordResetRequired(false) .build();
 * user = userRepository.save(user);
 * log.info("Supplier user created with ID: {}", user.getId());
 * 
 * // Create supplier profile Supplier supplier = Supplier.builder()
 * .userId(user.getId()) .companyName(request.getSupplierName())
 * .email(request.getEmail()) .phone(request.getPhone())
 * .address(request.getBusinessAddress()) .isVerified(false) .build();
 * supplierRepository.save(supplier); log.info("Supplier profile created");
 * 
 * // Generate tokens UserDetailsImpl userDetails = UserDetailsImpl.build(user);
 * String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(),
 * user.getRole().name(), null, user.getName()); String refreshToken =
 * generateRefreshToken(userDetails, user.getId());
 * 
 * return buildLoginResponse(user, accessToken, refreshToken); }
 * 
 * @Transactional public LoginResponse registerAdmin(RegisterAdminRequest
 * request) { log.info("Registering new admin with email: {}",
 * request.getEmail());
 * 
 * // Check if email already exists if
 * (userRepository.existsByEmail(request.getEmail())) { throw new
 * DuplicateResourceException("Email already registered"); }
 * 
 * // Verify company exists Company company =
 * companyRepository.findById(request.getCompanyId()) .orElseThrow(() -> new
 * ResourceNotFoundException("Company not found with ID: " +
 * request.getCompanyId()));
 * 
 * // Create admin user User user = User.builder() .name(request.getName())
 * .email(request.getEmail())
 * .password(passwordEncoder.encode(request.getPassword())) .role(Role.ADMIN)
 * .companyId(request.getCompanyId()) .isActive(true)
 * .isPasswordResetRequired(false) .build();
 * 
 * user = userRepository.save(user);
 * log.info("Admin user created with ID: {} for company: {}", user.getId(),
 * company.getName());
 * 
 * // Generate tokens UserDetailsImpl userDetails = UserDetailsImpl.build(user);
 * String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(),
 * user.getRole().name(), user.getCompanyId(), user.getName()); String
 * refreshToken = generateRefreshToken(userDetails, user.getId());
 * 
 * return buildLoginResponse(user, accessToken, refreshToken); }
 * 
 * public LoginResponse login(LoginRequest request) {
 * log.info("Login attempt for email: {}", request.getEmail());
 * 
 * Authentication authentication = authenticationManager.authenticate( new
 * UsernamePasswordAuthenticationToken(request.getEmail(),
 * request.getPassword()) );
 * 
 * SecurityContextHolder.getContext().setAuthentication(authentication);
 * UserDetailsImpl userDetails = (UserDetailsImpl)
 * authentication.getPrincipal();
 * 
 * User user = userRepository.findById(userDetails.getId()) .orElseThrow(() ->
 * new ResourceNotFoundException("User not found"));
 * 
 * if (!user.getIsActive()) {
 * log.warn("Login failed: Account deactivated for user: {}",
 * request.getEmail()); throw new
 * RuntimeException("Account is deactivated. Please contact administrator."); }
 * 
 * log.info("Login successful for user: {}", request.getEmail());
 * 
 * String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(),
 * user.getRole().name(), user.getCompanyId(), user.getName()); String
 * refreshToken = generateRefreshToken(userDetails, user.getId());
 * 
 * return buildLoginResponse(user, accessToken, refreshToken); }
 * 
 * @Transactional public LoginResponse refreshToken(String refreshToken) {
 * log.info("Refreshing token");
 * 
 * if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
 * refreshToken = refreshToken.substring(7); }
 * 
 * if (!jwtUtil.validateRefreshToken(refreshToken)) {
 * log.warn("Invalid refresh token"); throw new
 * RuntimeException("Invalid refresh token"); }
 * 
 * Long userId = jwtUtil.extractUserId(refreshToken);
 * 
 * RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
 * .orElseThrow(() -> new RuntimeException("Refresh token not found"));
 * 
 * if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
 * refreshTokenRepository.delete(storedToken);
 * log.warn("Refresh token expired for user ID: {}", userId); throw new
 * RuntimeException("Refresh token expired"); }
 * 
 * User user = userRepository.findById(userId) .orElseThrow(() -> new
 * ResourceNotFoundException("User not found"));
 * 
 * UserDetailsImpl userDetails = UserDetailsImpl.build(user); String
 * newAccessToken = jwtUtil.generateAccessToken(userDetails, user.getId(),
 * user.getRole().name(), user.getCompanyId(), user.getName()); String
 * newRefreshToken = generateRefreshToken(userDetails, user.getId());
 * 
 * // Delete old refresh token refreshTokenRepository.delete(storedToken);
 * 
 * log.info("Token refreshed successfully for user: {}", user.getEmail());
 * 
 * return buildLoginResponse(user, newAccessToken, newRefreshToken); }
 * 
 * @Transactional public void logout(String token) {
 * log.info("Logging out user");
 * 
 * if (token != null && token.startsWith("Bearer ")) { token =
 * token.substring(7); }
 * 
 * refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
 * refreshTokenRepository.delete(refreshToken);
 * log.info("Refresh token deleted"); }); }
 * 
 * @Transactional public void setPassword(SetPasswordRequest request, String
 * token) { log.info("Setting new password");
 * 
 * if (token != null && token.startsWith("Bearer ")) { token =
 * token.substring(7); }
 * 
 * String email = jwtUtil.extractUsername(token); User user =
 * userRepository.findByEmail(email) .orElseThrow(() -> new
 * ResourceNotFoundException("User not found"));
 * 
 * user.setPassword(passwordEncoder.encode(request.getNewPassword()));
 * user.setIsPasswordResetRequired(false); userRepository.save(user);
 * 
 * log.info("Password updated successfully for user: {}", email); }
 * 
 * private String generateRefreshToken(UserDetailsImpl userDetails, Long userId)
 * { String token = jwtUtil.generateRefreshToken(userDetails, userId);
 * 
 * RefreshToken refreshToken = RefreshToken.builder() .token(token)
 * .userId(userId) .expiryDate(LocalDateTime.now().plusDays(7)) .build();
 * 
 * refreshTokenRepository.save(refreshToken); return token; }
 * 
 * private LoginResponse buildLoginResponse(User user, String accessToken,
 * String refreshToken) { return LoginResponse.builder()
 * .accessToken(accessToken) .refreshToken(refreshToken) .tokenType("Bearer")
 * .userId(user.getId()) .email(user.getEmail()) .name(user.getName())
 * .role(user.getRole().name()) .companyId(user.getCompanyId())
 * .isPasswordResetRequired(user.getIsPasswordResetRequired()) .build(); }
 * 
 * 
 * }
 */











package com.inventory.modules.auth;

import com.inventory.common.enums.Role;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.auth.dto.*;
import com.inventory.modules.company.entities.Company;
import com.inventory.modules.company.repositories.CompanyRepository;
import com.inventory.modules.notification.dto.NotificationRequest;
import com.inventory.modules.notification.services.NotificationService;
import com.inventory.modules.supplier.entities.Supplier;
import com.inventory.modules.supplier.repositories.SupplierRepository;
import com.inventory.modules.user.entities.RefreshToken;
import com.inventory.modules.user.entities.User;
import com.inventory.modules.user.repositories.RefreshTokenRepository;
import com.inventory.modules.user.repositories.UserRepository;
import com.inventory.security.JwtUtil;
import com.inventory.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SupplierRepository supplierRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;  // ADD THIS
    
    @Transactional
    public LoginResponse registerOwner(RegisterOwnerRequest request) {
        log.info("Registering new owner with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        // Create company
        Company company = Company.builder()
            .name(request.getCompanyName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .build();
        company = companyRepository.save(company);
        log.info("Company created with ID: {}", company.getId());
        
        // Create user
        User user = User.builder()
            .name(request.getOwnerName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.OWNER)
            .companyId(company.getId())
            .isActive(true)
            .isPasswordResetRequired(false)
            .lastLoginAt(LocalDateTime.now())
            .build();
        user = userRepository.save(user);
        log.info("Owner user created with ID: {}", user.getId());
        
        // Send welcome notification
        sendWelcomeNotification(user, "Owner");
        
        // Generate tokens
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), 
            user.getRole().name(), user.getCompanyId(), user.getName());
        String refreshToken = generateRefreshToken(userDetails, user.getId());
        
        return buildLoginResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public LoginResponse registerSupplier(RegisterSupplierRequest request) {
        log.info("Registering new supplier with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        // Create user
        User user = User.builder()
            .name(request.getSupplierName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.SUPPLIER)
            .companyId(null)
            .isActive(true)
            .isPasswordResetRequired(false)
            .lastLoginAt(LocalDateTime.now())
            .build();
        user = userRepository.save(user);
        log.info("Supplier user created with ID: {}", user.getId());
        
        // Create supplier profile
        Supplier supplier = Supplier.builder()
            .userId(user.getId())
            .companyName(request.getSupplierName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getBusinessAddress())
            .isVerified(false)
            .build();
        supplierRepository.save(supplier);
        log.info("Supplier profile created");
        
        // Send welcome notification
        sendWelcomeNotification(user, "Supplier");
        
        // Generate tokens
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), 
            user.getRole().name(), null, user.getName());
        String refreshToken = generateRefreshToken(userDetails, user.getId());
        
        return buildLoginResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public LoginResponse registerAdmin(RegisterAdminRequest request) {
        log.info("Registering new admin with email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        // Verify company exists
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));
        
        // Create admin user
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ADMIN)
            .companyId(request.getCompanyId())
            .isActive(true)
            .isPasswordResetRequired(false)
            .lastLoginAt(LocalDateTime.now())
            .build();
        
        user = userRepository.save(user);
        log.info("Admin user created with ID: {} for company: {}", user.getId(), company.getName());
        
        // Send welcome notification
        sendWelcomeNotification(user, "Admin");
        
        // Send notification to company owner
        notifyCompanyOwner(user.getCompanyId(), user.getName(), "Admin");
        
        // Generate tokens
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), 
            user.getRole().name(), user.getCompanyId(), user.getName());
        String refreshToken = generateRefreshToken(userDetails, user.getId());
        
        return buildLoginResponse(user, accessToken, refreshToken);
    }
    
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!user.getIsActive()) {
            log.warn("Login failed: Account deactivated for user: {}", request.getEmail());
            throw new RuntimeException("Account is deactivated. Please contact administrator.");
        }
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Login successful for user: {}", request.getEmail());
        
        // Send login notification
        sendLoginNotification(user);
        
        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), 
            user.getRole().name(), user.getCompanyId(), user.getName());
        String refreshToken = generateRefreshToken(userDetails, user.getId());
        
        return buildLoginResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new RuntimeException("Invalid refresh token");
        }
        
        Long userId = jwtUtil.extractUserId(refreshToken);
        
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            log.warn("Refresh token expired for user ID: {}", userId);
            throw new RuntimeException("Refresh token expired");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), 
            user.getRole().name(), user.getCompanyId(), user.getName());
        String newRefreshToken = generateRefreshToken(userDetails, user.getId());
        
        // Delete old refresh token
        refreshTokenRepository.delete(storedToken);
        
        log.info("Token refreshed successfully for user: {}", user.getEmail());
        
        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }
    
    @Transactional
    public void logout(String token) {
        log.info("Logging out user");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            log.info("Refresh token deleted");
        });
    }
    
    @Transactional
    public void setPassword(SetPasswordRequest request, String token) {
        log.info("Setting new password");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsPasswordResetRequired(false);
        userRepository.save(user);
        
        log.info("Password updated successfully for user: {}", email);
        
        // Send password update notification
        sendPasswordUpdateNotification(user);
    }
    
    // ========== NOTIFICATION METHODS ==========
    
    private void sendWelcomeNotification(User user, String role) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle("🎉 Welcome to Inventory Management System!");
            request.setMessage("Welcome " + user.getName() + "! Your " + role + " account has been created successfully. " +
                              "You can now start managing your inventory.");
            request.setType("SUCCESS");
            request.setLink("/dashboard");
            request.setIcon("celebration");
            
            notificationService.createNotification(user.getId(), user.getCompanyId(), request);
            log.info("Welcome notification sent to user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send welcome notification: {}", e.getMessage());
        }
    }
    
    private void sendLoginNotification(User user) {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            boolean isFirstLogin = user.getLastLoginAt() == null;
            
            String title;
            String message;
            String icon = "check_circle";
            String type = "SUCCESS";
            
            switch (user.getRole().toString()) {
                case "OWNER":
                case "ADMIN":
                    if (isFirstLogin) {
                        title = "🏢 Welcome to Your Company!";
                        message = "Welcome " + user.getName() + "! Start managing your company today.";
                    } else {
                        title = "🏢 Welcome Back, Admin!";
                        message = "You logged in at " + time + ". Your company is ready for action.";
                    }
                    break;
                    
                case "SUPPLIER":
                    if (isFirstLogin) {
                        title = "📦 Welcome to the Platform!";
                        message = "Welcome as a Supplier! Start listing your products today.";
                    } else {
                        title = "📦 Welcome Back, Supplier!";
                        message = "You logged in at " + time + ". Your products are ready for review.";
                    }
                    break;
                    
                case "MANAGER":
                    if (isFirstLogin) {
                        title = "📊 Welcome to Your Dashboard!";
                        message = "Welcome as a Manager! You can now manage inventory and sales.";
                    } else {
                        title = "📊 Welcome Back, Manager!";
                        message = "You logged in at " + time + ". Check your inventory reports.";
                    }
                    break;
                    
                default:
                    if (isFirstLogin) {
                        title = "👋 Welcome to the Team!";
                        message = "Welcome " + user.getName() + "! You are now part of the system.";
                    } else {
                        title = "👋 Welcome Back!";
                        message = "Welcome back, " + user.getName() + "! You logged in at " + time + ".";
                    }
            }
            
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink("/dashboard");
            request.setIcon(icon);
            
            notificationService.createNotification(user.getId(), user.getCompanyId(), request);
            log.info("Login notification sent to user: {}", user.getId());
            
        } catch (Exception e) {
            log.error("Failed to send login notification: {}", e.getMessage());
        }
    }
    
    private void sendPasswordUpdateNotification(User user) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle("🔐 Password Updated");
            request.setMessage("Your password has been changed successfully. If you didn't do this, please contact support immediately.");
            request.setType("SUCCESS");
            request.setLink("/profile");
            request.setIcon("lock");
            
            notificationService.createNotification(user.getId(), user.getCompanyId(), request);
            log.info("Password update notification sent to user: {}", user.getId());
            
        } catch (Exception e) {
            log.error("Failed to send password update notification: {}", e.getMessage());
        }
    }
    private void notifyCompanyOwner(Long companyId, String adminName, String role) {
        try {
            // Find company owner - using findFirstByCompanyIdAndRole
            User owner = userRepository.findFirstByCompanyIdAndRole(companyId, Role.OWNER)
                .orElse(null);
            
            if (owner != null) {
                NotificationRequest request = new NotificationRequest();
                request.setTitle("👤 New " + role + " Added");
                request.setMessage(adminName + " has been added as a " + role + " to your company.");
                request.setType("INFO");
                request.setLink("/staff");
                request.setIcon("person_add");
                
                notificationService.createNotification(owner.getId(), companyId, request);
                log.info("Company owner notified about new admin: {}", adminName);
            } else {
                log.warn("No owner found for company ID: {}", companyId);
            }
        } catch (Exception e) {
            log.error("Failed to notify company owner: {}", e.getMessage());
        }
    }
    // ========== HELPER METHODS ==========
    
    private String generateRefreshToken(UserDetailsImpl userDetails, Long userId) {
        String token = jwtUtil.generateRefreshToken(userDetails, userId);
        
        RefreshToken refreshToken = RefreshToken.builder()
            .token(token)
            .userId(userId)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole().name())
            .companyId(user.getCompanyId())
            .isPasswordResetRequired(user.getIsPasswordResetRequired())
            .build();
    }
}
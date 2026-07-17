/*
 * package com.inventory.modules.user.entities;
 * 
 * import com.inventory.common.enums.Role; import jakarta.persistence.*; import
 * lombok.AllArgsConstructor; import lombok.Builder; import lombok.Data; import
 * lombok.NoArgsConstructor; import org.hibernate.annotations.CreationTimestamp;
 * import org.hibernate.annotations.UpdateTimestamp; import
 * java.time.LocalDateTime;
 * 
 * @Entity
 * 
 * @Table(name = "users")
 * 
 * @Data
 * 
 * @Builder
 * 
 * @NoArgsConstructor
 * 
 * @AllArgsConstructor public class User {
 * 
 * @Id
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 * 
 * @Column(nullable = false, length = 100) private String name;
 * 
 * @Column(nullable = false, unique = true, length = 100) private String email;
 * 
 * @Column(nullable = false) private String password;
 * 
 * @Enumerated(EnumType.STRING)
 * 
 * @Column(nullable = false) private Role role;
 * 
 * @Column(name = "company_id") private Long companyId;
 * 
 * @Column(name = "department_id") // ← ADD THIS FIELD private Long
 * departmentId;
 * 
 * @Column(name = "is_active") private Boolean isActive = true;
 * 
 * @Column(name = "is_password_reset_required") private Boolean
 * isPasswordResetRequired = true;
 * 
 * @CreationTimestamp
 * 
 * @Column(name = "created_at", updatable = false) private LocalDateTime
 * createdAt;
 * 
 * @UpdateTimestamp
 * 
 * @Column(name = "updated_at") private LocalDateTime updatedAt; }
 */









package com.inventory.modules.user.entities;

import com.inventory.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name = "department_id")
    private Long departmentId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_password_reset_required")
    private Boolean isPasswordResetRequired = false;  // Changed default to false
    
    @Column(name = "last_login_at")  // ADD THIS FIELD
    private LocalDateTime lastLoginAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper method to check if user is active
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    // Helper method to check if password reset is required
    public boolean isPasswordResetRequired() {
        return isPasswordResetRequired != null && isPasswordResetRequired;
    }
}
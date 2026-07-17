/*
 * package com.inventory.modules.user.repositories;
 * 
 * import com.inventory.common.enums.Role; import
 * com.inventory.modules.user.entities.User; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.stereotype.Repository; import java.util.List; import
 * java.util.Optional;
 * 
 * @Repository public interface UserRepository extends JpaRepository<User, Long>
 * {
 * 
 * Optional<User> findByEmail(String email);
 * 
 * boolean existsByEmail(String email);
 * 
 * List<User> findByCompanyId(Long companyId);
 * 
 * List<User> findByCompanyIdAndRole(Long companyId, Role role);
 * 
 * List<User> findByRole(Role role);
 * 
 * Optional<User> findByEmailAndRole(String email, Role role); }
 */









package com.inventory.modules.user.repositories;

import com.inventory.common.enums.Role;
import com.inventory.modules.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByCompanyId(Long companyId);
    
    List<User> findByCompanyIdAndRole(Long companyId, Role role);
    
    // ADD THIS METHOD - Returns Optional<User> for single result
    Optional<User> findFirstByCompanyIdAndRole(Long companyId, Role role);
    
    List<User> findByRole(Role role);
    
    Optional<User> findByEmailAndRole(String email, Role role);
    
    @Query("SELECT u FROM User u WHERE u.companyId = :companyId AND u.role IN :roles")
    List<User> findByCompanyIdAndRoleIn(@Param("companyId") Long companyId, @Param("roles") List<Role> roles);
    
    List<User> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    List<User> findByIsPasswordResetRequiredTrue();
    
    List<User> findByCompanyIdAndIsActive(Long companyId, Boolean isActive);
    
    List<User> findByCompanyIdAndRoleAndIsActiveTrue(Long companyId, Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.companyId = :companyId AND u.isActive = true")
    Long countActiveUsersByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.companyId = :companyId AND u.role = :role AND u.isActive = true")
    Long countUsersByCompanyIdAndRole(@Param("companyId") Long companyId, @Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<User> findRecentActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.companyId = :companyId ORDER BY u.lastLoginAt DESC")
    List<User> findByCompanyIdOrderByLastLoginAtDesc(@Param("companyId") Long companyId);
    
    boolean existsByEmailAndCompanyId(String email, Long companyId);
    
    @Query("SELECT u FROM User u WHERE u.companyId = :companyId AND u.isActive = true AND u.role IN :roles")
    List<User> findActiveUsersByCompanyIdAndRoleIn(@Param("companyId") Long companyId, @Param("roles") List<Role> roles);
}
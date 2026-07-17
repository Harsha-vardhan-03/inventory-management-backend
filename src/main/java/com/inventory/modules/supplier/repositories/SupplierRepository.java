package com.inventory.modules.supplier.repositories;

import com.inventory.modules.supplier.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findByUserId(Long userId);
    
    Optional<Supplier> findByEmail(String email);
    
    List<Supplier> findByIsVerified(Boolean isVerified);
    
    boolean existsByEmail(String email);
}
package com.inventory.modules.sales.repositories;

import com.inventory.modules.sales.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByCompanyId(Long companyId);
    
    Optional<Customer> findByCompanyIdAndEmail(Long companyId, String email);
    
    List<Customer> findByCompanyIdAndNameContainingIgnoreCase(Long companyId, String name);
    
    boolean existsByCompanyIdAndEmail(Long companyId, String email);
}
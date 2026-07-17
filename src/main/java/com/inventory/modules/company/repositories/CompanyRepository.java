package com.inventory.modules.company.repositories;

import com.inventory.modules.company.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    Optional<Company> findByEmail(String email);
    
    boolean existsByName(String name);
    
    boolean existsByEmail(String email);
}
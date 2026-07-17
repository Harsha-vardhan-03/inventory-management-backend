package com.inventory.modules.company.repositories;

import com.inventory.modules.company.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByCompanyId(Long companyId);
    
    boolean existsByCompanyIdAndName(Long companyId, String name);
    
    void deleteByCompanyId(Long companyId);
}
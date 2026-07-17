package com.inventory.modules.supplier.repositories;

import com.inventory.common.enums.LinkStatus;
import com.inventory.modules.supplier.entities.CompanySupplierLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySupplierLinkRepository extends JpaRepository<CompanySupplierLink, Long> {
    
    Optional<CompanySupplierLink> findByCompanyIdAndSupplierId(Long companyId, Long supplierId);
    
    List<CompanySupplierLink> findByCompanyId(Long companyId);
    
    List<CompanySupplierLink> findBySupplierId(Long supplierId);
    
    List<CompanySupplierLink> findByCompanyIdAndStatus(Long companyId, LinkStatus status);
    
    List<CompanySupplierLink> findBySupplierIdAndStatus(Long supplierId, LinkStatus status);
    
    boolean existsByCompanyIdAndSupplierIdAndStatus(Long companyId, Long supplierId, LinkStatus status);
}
package com.inventory.modules.purchase.repositories;

import com.inventory.common.enums.OrderStatus;
import com.inventory.modules.purchase.entities.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    
    List<PurchaseRequest> findByCompanyId(Long companyId);
    
    List<PurchaseRequest> findBySupplierId(Long supplierId);
    
    List<PurchaseRequest> findByCompanyIdAndStatus(Long companyId, OrderStatus status);
    
    List<PurchaseRequest> findBySupplierIdAndStatus(Long supplierId, OrderStatus status);
    
    List<PurchaseRequest> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    List<PurchaseRequest> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);
    
    @Query("SELECT COALESCE(SUM(pri.requestedQty), 0) FROM PurchaseRequestItem pri " +
           "JOIN PurchaseRequest pr ON pri.purchaseRequestId = pr.id " +
           "WHERE pr.companyId = :companyId AND pr.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalItemsPurchased(@Param("companyId") Long companyId, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}
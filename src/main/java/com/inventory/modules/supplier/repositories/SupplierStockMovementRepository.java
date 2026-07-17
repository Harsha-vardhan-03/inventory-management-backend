package com.inventory.modules.supplier.repositories;

import com.inventory.modules.supplier.entities.SupplierStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierStockMovementRepository extends JpaRepository<SupplierStockMovement, Long> {
    
    List<SupplierStockMovement> findBySupplierProductId(Long supplierProductId);
    
    List<SupplierStockMovement> findBySupplierProductIdOrderByCreatedAtDesc(Long supplierProductId);
    
    @Query("SELECT s FROM SupplierStockMovement s WHERE s.supplierProductId IN " +
           "(SELECT p.id FROM SupplierProduct p WHERE p.supplierId = :supplierId) " +
           "ORDER BY s.createdAt DESC")
    List<SupplierStockMovement> findBySupplierId(@Param("supplierId") Long supplierId);
    
    @Query("SELECT s FROM SupplierStockMovement s WHERE s.supplierProductId = :productId " +
           "AND s.createdAt BETWEEN :startDate AND :endDate")
    List<SupplierStockMovement> findByProductIdAndDateRange(@Param("productId") Long productId,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);
}
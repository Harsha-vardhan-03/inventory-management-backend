package com.inventory.modules.inventory.repositories;

import com.inventory.common.enums.StockMovementType;
import com.inventory.modules.inventory.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByProductId(Long productId);
    
    List<StockMovement> findByCompanyId(Long companyId);
    
    List<StockMovement> findByCompanyIdAndType(Long companyId, StockMovementType type);
    
    @Query("SELECT s FROM StockMovement s WHERE s.companyId = :companyId AND s.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByDateRange(@Param("companyId") Long companyId, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(s.quantity) FROM StockMovement s WHERE s.productId = :productId AND s.type = :type")
    Integer getTotalQuantityByType(@Param("productId") Long productId, @Param("type") StockMovementType type);
}
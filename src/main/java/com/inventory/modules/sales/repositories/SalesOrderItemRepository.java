package com.inventory.modules.sales.repositories;

import com.inventory.modules.sales.entities.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
    
    List<SalesOrderItem> findBySalesOrderId(Long salesOrderId);
    
    @Query("SELECT soi.productId, SUM(soi.quantity) as totalQuantity FROM SalesOrderItem soi " +
           "JOIN SalesOrder so ON soi.salesOrderId = so.id " +
           "WHERE so.companyId = :companyId GROUP BY soi.productId ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(@Param("companyId") Long companyId);
    
    @Query("SELECT COALESCE(SUM(soi.quantity), 0) FROM SalesOrderItem soi " +
           "JOIN SalesOrder so ON soi.salesOrderId = so.id " +
           "WHERE so.companyId = :companyId AND so.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalItemsSold(@Param("companyId") Long companyId, 
                              @Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
}
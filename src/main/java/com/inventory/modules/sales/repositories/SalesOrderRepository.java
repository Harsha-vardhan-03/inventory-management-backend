package com.inventory.modules.sales.repositories;

import com.inventory.modules.sales.entities.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    
    List<SalesOrder> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    List<SalesOrder> findByCustomerId(Long customerId);
    
    @Query("SELECT COALESCE(SUM(s.finalAmount), 0) FROM SalesOrder s " +
           "WHERE s.companyId = :companyId AND s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenue(@Param("companyId") Long companyId, 
                               @Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(COUNT(s), 0) FROM SalesOrder s " +
           "WHERE s.companyId = :companyId AND s.createdAt BETWEEN :startDate AND :endDate")
    Long getOrderCount(@Param("companyId") Long companyId, 
                       @Param("startDate") LocalDateTime startDate, 
                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(s.discount), 0) FROM SalesOrder s " +
           "WHERE s.companyId = :companyId AND s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalDiscount(@Param("companyId") Long companyId, 
                                @Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);
}
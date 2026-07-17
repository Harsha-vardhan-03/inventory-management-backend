package com.inventory.modules.supplier.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_stock_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "supplier_product_id", nullable = false)
    private Long supplierProductId;
    
    @Column(nullable = false)
    private String type; // STOCK_IN, STOCK_OUT, ADJUSTMENT
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "reference_type")
    private String referenceType; // PURCHASE_ORDER, MANUAL_ADJUSTMENT, RETURN
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    private String notes;
    
    @Column(name = "performed_by")
    private Long performedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
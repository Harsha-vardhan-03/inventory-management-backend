package com.inventory.modules.inventory.entities;

import com.inventory.common.enums.StockMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType type;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "reference_type", length = 100)
    private String referenceType;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "performed_by", nullable = false)
    private Long performedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
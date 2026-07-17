/*
 * package com.inventory.modules.supplier.entities;
 * 
 * import jakarta.persistence.*; import lombok.AllArgsConstructor; import
 * lombok.Builder; import lombok.Data; import lombok.NoArgsConstructor; import
 * org.hibernate.annotations.CreationTimestamp; import
 * org.hibernate.annotations.UpdateTimestamp; import java.math.BigDecimal;
 * import java.time.LocalDateTime;
 * 
 * @Entity
 * 
 * @Table(name = "supplier_products")
 * 
 * @Data
 * 
 * @Builder
 * 
 * @NoArgsConstructor
 * 
 * @AllArgsConstructor public class SupplierProduct {
 * 
 * @Id
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 * 
 * @Column(name = "supplier_id", nullable = false) private Long supplierId;
 * 
 * @Column(nullable = false, length = 200) private String name;
 * 
 * @Column(columnDefinition = "TEXT") private String description;
 * 
 * @Column(length = 50) private String unit;
 * 
 * @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
 * private BigDecimal unitPrice;
 * 
 * @Column(name = "minimum_stock_level") private Integer minimumStockLevel = 5;
 * 
 * @Column(name = "available_qty") private Integer availableQty = 0;
 * 
 * @Column(length = 100) private String category;
 * 
 * @CreationTimestamp
 * 
 * @Column(name = "created_at", updatable = false) private LocalDateTime
 * createdAt;
 * 
 * @UpdateTimestamp
 * 
 * @Column(name = "updated_at") private LocalDateTime updatedAt;
 * 
 * 
 * @Column(name = "is_active") private Boolean isActive = true; }
 */


package com.inventory.modules.supplier.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String unit;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Builder.Default
    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 5;

    @Builder.Default
    @Column(name = "available_qty", nullable = false)
    private Integer availableQty = 0;

    @Column(length = 100)
    private String category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
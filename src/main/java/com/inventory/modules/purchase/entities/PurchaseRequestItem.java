package com.inventory.modules.purchase.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_request_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "purchase_request_id", nullable = false)
    private Long purchaseRequestId;
    
    @Column(name = "supplier_product_id", nullable = false)
    private Long supplierProductId;
    
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    @Column(name = "requested_qty", nullable = false)
    private Integer requestedQty;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(name = "received_qty")
    private Integer receivedQty;
}
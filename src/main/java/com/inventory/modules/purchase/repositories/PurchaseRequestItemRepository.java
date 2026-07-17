package com.inventory.modules.purchase.repositories;

import com.inventory.modules.purchase.entities.PurchaseRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {
    
    List<PurchaseRequestItem> findByPurchaseRequestId(Long purchaseRequestId);
    
    void deleteByPurchaseRequestId(Long purchaseRequestId);
}
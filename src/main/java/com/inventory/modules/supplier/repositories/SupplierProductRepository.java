/*
 * package com.inventory.modules.supplier.repositories;
 * 
 * import com.inventory.modules.supplier.entities.SupplierProduct; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.stereotype.Repository; import java.util.List;
 * 
 * @Repository public interface SupplierProductRepository extends
 * JpaRepository<SupplierProduct, Long> {
 * 
 * List<SupplierProduct> findBySupplierId(Long supplierId);
 * 
 * List<SupplierProduct> findBySupplierIdAndCategory(Long supplierId, String
 * category);
 * 
 * List<SupplierProduct> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);
 * }
 */









package com.inventory.modules.supplier.repositories;

import com.inventory.modules.supplier.entities.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    // All products for supplier
    List<SupplierProduct> findBySupplierId(Long supplierId);

    // All products ordered by latest first
    List<SupplierProduct> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    // Products by category
    List<SupplierProduct> findBySupplierIdAndCategory(Long supplierId, String category);

    // Only active products
    List<SupplierProduct> findBySupplierIdAndIsActiveTrue(Long supplierId);

    // Active products by category
    List<SupplierProduct> findBySupplierIdAndCategoryAndIsActiveTrue(
            Long supplierId,
            String category
    );

    // Active products ordered by latest first
    List<SupplierProduct> findBySupplierIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long supplierId
    );
}
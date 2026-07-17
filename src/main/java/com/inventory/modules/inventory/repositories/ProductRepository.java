/*
 * package com.inventory.modules.inventory.repositories;
 * 
 * import com.inventory.modules.inventory.entities.Product; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.data.jpa.repository.Query; import
 * org.springframework.data.repository.query.Param; import
 * org.springframework.stereotype.Repository; import java.util.List; import
 * java.util.Optional;
 * 
 * @Repository public interface ProductRepository extends JpaRepository<Product,
 * Long> {
 * 
 * List<Product> findByCompanyId(Long companyId);
 * 
 * Optional<Product> findByCompanyIdAndName(Long companyId, String name);
 * 
 * Optional<Product> findByCompanyIdAndSku(Long companyId, String sku);
 * 
 * List<Product> findByCompanyIdAndIsActiveTrue(Long companyId);
 * 
 * @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.currentStock < p.minimumStockLevel"
 * ) List<Product> findLowStockProducts(@Param("companyId") Long companyId);
 * 
 * List<Product> findByCompanyIdAndCategory(Long companyId, String category);
 * 
 * boolean existsByCompanyIdAndSku(Long companyId, String sku); }
 */








package com.inventory.modules.inventory.repositories;

import com.inventory.modules.inventory.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find all products for a company
    List<Product> findByCompanyId(Long companyId);
    
    // Find product by company ID and name (used for purchase delivery)
    Optional<Product> findByCompanyIdAndName(Long companyId, String name);
    
    // Find product by company ID and SKU
    Optional<Product> findByCompanyIdAndSku(Long companyId, String sku);
    
    // Find all active products for a company
    List<Product> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    // Find low stock products (current stock < minimum stock level)
    @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.currentStock < p.minimumStockLevel AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("companyId") Long companyId);
    
    // Find out of stock products (current stock = 0)
    @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.currentStock = 0 AND p.isActive = true")
    List<Product> findOutOfStockProducts(@Param("companyId") Long companyId);
    
    // Find products by category
    List<Product> findByCompanyIdAndCategory(Long companyId, String category);
    
    // Check if product exists by company ID and SKU
    boolean existsByCompanyIdAndSku(Long companyId, String sku);
    
    // Count products by company
    @Query("SELECT COUNT(p) FROM Product p WHERE p.companyId = :companyId AND p.isActive = true")
    Long countActiveProductsByCompanyId(@Param("companyId") Long companyId);
    
    // Find products with stock above a certain threshold
    @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.currentStock > :threshold AND p.isActive = true")
    List<Product> findProductsWithStockAbove(@Param("companyId") Long companyId, @Param("threshold") Integer threshold);
    
    // Find products with stock below a certain threshold
    @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.currentStock < :threshold AND p.isActive = true")
    List<Product> findProductsWithStockBelow(@Param("companyId") Long companyId, @Param("threshold") Integer threshold);
}
/*
 * package com.inventory.modules.inventory.services;
 * 
 * import com.inventory.common.enums.StockMovementType; import
 * com.inventory.common.exception.ResourceNotFoundException; import
 * com.inventory.modules.inventory.dto.*; import
 * com.inventory.modules.inventory.entities.Product; import
 * com.inventory.modules.inventory.entities.StockMovement; import
 * com.inventory.modules.inventory.repositories.ProductRepository; import
 * com.inventory.modules.inventory.repositories.StockMovementRepository; import
 * com.inventory.modules.user.entities.User; import
 * com.inventory.modules.user.repositories.UserRepository; import
 * lombok.RequiredArgsConstructor; import
 * org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional; import
 * java.util.List; import java.util.UUID; import java.util.stream.Collectors;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor public class InventoryService {
 * 
 * private final ProductRepository productRepository; private final
 * StockMovementRepository stockMovementRepository; private final UserRepository
 * userRepository;
 * 
 * @Transactional public ProductResponse addProduct(Long companyId,
 * ProductRequest request) { // Generate SKU String sku =
 * generateSKU(request.getName(), companyId);
 * 
 * Product product = Product.builder() .companyId(companyId)
 * .name(request.getName()) .description(request.getDescription()) .sku(sku)
 * .category(request.getCategory()) .unit(request.getUnit())
 * .unitPrice(request.getUnitPrice()) .sellingPrice(request.getSellingPrice())
 * .currentStock(request.getCurrentStock())
 * .minimumStockLevel(request.getMinimumStockLevel())
 * .imageUrl(request.getImageUrl()) .isActive(true) .build();
 * 
 * product = productRepository.save(product);
 * 
 * // If initial stock > 0, create stock movement if (request.getCurrentStock()
 * > 0) { StockMovement movement = StockMovement.builder()
 * .productId(product.getId()) .companyId(companyId)
 * .type(StockMovementType.STOCK_IN) .quantity(request.getCurrentStock())
 * .notes("Initial stock added") .build();
 * stockMovementRepository.save(movement); }
 * 
 * return mapToResponse(product); }
 * 
 * @Transactional public ProductResponse updateProduct(Long companyId, Long
 * productId, ProductRequest request) { Product product =
 * productRepository.findById(productId) .orElseThrow(() -> new
 * ResourceNotFoundException("Product not found"));
 * 
 * if (!product.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Product not found for this company"); }
 * 
 * product.setName(request.getName());
 * product.setDescription(request.getDescription());
 * product.setCategory(request.getCategory());
 * product.setUnit(request.getUnit());
 * product.setUnitPrice(request.getUnitPrice());
 * product.setSellingPrice(request.getSellingPrice());
 * product.setMinimumStockLevel(request.getMinimumStockLevel());
 * product.setImageUrl(request.getImageUrl());
 * 
 * product = productRepository.save(product); return mapToResponse(product); }
 * 
 * @Transactional public void deleteProduct(Long companyId, Long productId) {
 * Product product = productRepository.findById(productId) .orElseThrow(() ->
 * new ResourceNotFoundException("Product not found"));
 * 
 * if (!product.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Product not found for this company"); }
 * 
 * product.setIsActive(false); productRepository.save(product); }
 * 
 * @Transactional public StockMovementResponse adjustStock(Long companyId, Long
 * userId, StockMovementRequest request) { Product product =
 * productRepository.findById(request.getProductId()) .orElseThrow(() -> new
 * ResourceNotFoundException("Product not found"));
 * 
 * if (!product.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Product not found for this company"); }
 * 
 * int oldStock = product.getCurrentStock(); int newStock = oldStock;
 * 
 * if (request.getType() == StockMovementType.STOCK_IN) { newStock = oldStock +
 * request.getQuantity(); } else if (request.getType() ==
 * StockMovementType.STOCK_OUT) { if (oldStock < request.getQuantity()) { throw
 * new IllegalArgumentException("Insufficient stock. Available: " + oldStock); }
 * newStock = oldStock - request.getQuantity(); } else if (request.getType() ==
 * StockMovementType.ADJUSTMENT) { newStock = request.getQuantity(); }
 * 
 * product.setCurrentStock(newStock); productRepository.save(product);
 * 
 * StockMovement movement = StockMovement.builder() .productId(product.getId())
 * .companyId(companyId) .type(request.getType())
 * .quantity(request.getQuantity()) .notes(request.getNotes())
 * .performedBy(userId) .build();
 * 
 * movement = stockMovementRepository.save(movement);
 * 
 * return mapToMovementResponse(movement); }
 * 
 * public List<ProductResponse> getCompanyProducts(Long companyId) { return
 * productRepository.findByCompanyIdAndIsActiveTrue(companyId) .stream()
 * .map(this::mapToResponse) .collect(Collectors.toList()); }
 * 
 * public ProductResponse getProductById(Long companyId, Long productId) {
 * Product product = productRepository.findById(productId) .orElseThrow(() ->
 * new ResourceNotFoundException("Product not found"));
 * 
 * if (!product.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Product not found for this company"); }
 * 
 * return mapToResponse(product); }
 * 
 * public List<ProductResponse> getLowStockProducts(Long companyId) { return
 * productRepository.findLowStockProducts(companyId) .stream()
 * .map(this::mapToResponse) .collect(Collectors.toList()); }
 * 
 * public List<StockMovementResponse> getProductMovements(Long companyId, Long
 * productId) { Product product = productRepository.findById(productId)
 * .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
 * 
 * if (!product.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Product not found for this company"); }
 * 
 * return stockMovementRepository.findByProductId(productId) .stream()
 * .map(this::mapToMovementResponse) .collect(Collectors.toList()); }
 * 
 * public List<StockMovementResponse> getAllMovements(Long companyId) { return
 * stockMovementRepository.findByCompanyId(companyId) .stream()
 * .map(this::mapToMovementResponse) .collect(Collectors.toList()); }
 * 
 * private String generateSKU(String productName, Long companyId) { String base
 * = productName.toUpperCase().replaceAll("[^A-Za-z0-9]", "_"); String uniqueId
 * = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); return
 * base.substring(0, Math.min(10, base.length())) + "_" + uniqueId; }
 * 
 * private ProductResponse mapToResponse(Product product) { return
 * ProductResponse.builder() .id(product.getId()) .name(product.getName())
 * .description(product.getDescription()) .sku(product.getSku())
 * .category(product.getCategory()) .unit(product.getUnit())
 * .unitPrice(product.getUnitPrice()) .sellingPrice(product.getSellingPrice())
 * .currentStock(product.getCurrentStock())
 * .minimumStockLevel(product.getMinimumStockLevel())
 * .isLowStock(product.getCurrentStock() < product.getMinimumStockLevel())
 * .imageUrl(product.getImageUrl()) .isActive(product.getIsActive())
 * .createdAt(product.getCreatedAt()) .updatedAt(product.getUpdatedAt())
 * .build(); }
 * 
 * private StockMovementResponse mapToMovementResponse(StockMovement movement) {
 * Product product =
 * productRepository.findById(movement.getProductId()).orElse(null); User user =
 * userRepository.findById(movement.getPerformedBy()).orElse(null);
 * 
 * return StockMovementResponse.builder() .id(movement.getId())
 * .productId(movement.getProductId()) .productName(product != null ?
 * product.getName() : null) .type(movement.getType())
 * .quantity(movement.getQuantity()) .referenceType(movement.getReferenceType())
 * .notes(movement.getNotes()) .performedByName(user != null ? user.getName() :
 * null) .createdAt(movement.getCreatedAt()) .build(); } }
 */






package com.inventory.modules.inventory.services;

import com.inventory.common.enums.Role;
import com.inventory.common.enums.StockMovementType;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.inventory.dto.*;
import com.inventory.modules.inventory.entities.Product;
import com.inventory.modules.inventory.entities.StockMovement;
import com.inventory.modules.inventory.repositories.ProductRepository;
import com.inventory.modules.inventory.repositories.StockMovementRepository;
import com.inventory.modules.notification.dto.NotificationRequest;
import com.inventory.modules.notification.services.NotificationService;
import com.inventory.modules.user.entities.User;
import com.inventory.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;  // ADD THIS
    
    @Transactional
    public ProductResponse addProduct(Long companyId, ProductRequest request) {
        log.info("Adding product for company: {}", companyId);
        
        // Generate SKU
        String sku = generateSKU(request.getName(), companyId);
        
        Product product = Product.builder()
            .companyId(companyId)
            .name(request.getName())
            .description(request.getDescription())
            .sku(sku)
            .category(request.getCategory())
            .unit(request.getUnit())
            .unitPrice(request.getUnitPrice())
            .sellingPrice(request.getSellingPrice())
            .currentStock(request.getCurrentStock())
            .minimumStockLevel(request.getMinimumStockLevel())
            .imageUrl(request.getImageUrl())
            .isActive(true)
            .build();
        
        product = productRepository.save(product);
        
        // If initial stock > 0, create stock movement
        if (request.getCurrentStock() > 0) {
            StockMovement movement = StockMovement.builder()
                .productId(product.getId())
                .companyId(companyId)
                .type(StockMovementType.STOCK_IN)
                .quantity(request.getCurrentStock())
                .notes("Initial stock added")
                .build();
            stockMovementRepository.save(movement);
        }
        
        // Send notification
        sendProductNotification(companyId, 
            "🆕 New Product Added", 
            "New product '" + product.getName() + "' has been added to inventory with SKU: " + product.getSku(),
            "INVENTORY", "/inventory", "inventory_2");
        
        return mapToResponse(product);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long companyId, Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Product not found for this company");
        }
        
        String oldName = product.getName();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setUnit(request.getUnit());
        product.setUnitPrice(request.getUnitPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setMinimumStockLevel(request.getMinimumStockLevel());
        product.setImageUrl(request.getImageUrl());
        
        product = productRepository.save(product);
        
        // Send notification
        sendProductNotification(companyId, 
            "📝 Product Updated", 
            "Product '" + oldName + "' has been updated successfully.",
            "INVENTORY", "/inventory", "inventory_2");
        
        return mapToResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long companyId, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Product not found for this company");
        }
        
        product.setIsActive(false);
        productRepository.save(product);
        
        // Send notification
        sendProductNotification(companyId, 
            "🗑️ Product Deactivated", 
            "Product '" + product.getName() + "' has been deactivated.",
            "INVENTORY", "/inventory", "inventory_2");
    }
    
    @Transactional
    public StockMovementResponse adjustStock(Long companyId, Long userId, StockMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Product not found for this company");
        }
        
        int oldStock = product.getCurrentStock();
        int newStock = oldStock;
        String movementType = "";
        
        if (request.getType() == StockMovementType.STOCK_IN) {
            newStock = oldStock + request.getQuantity();
            movementType = "added";
        } else if (request.getType() == StockMovementType.STOCK_OUT) {
            if (oldStock < request.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + oldStock);
            }
            newStock = oldStock - request.getQuantity();
            movementType = "removed";
        } else if (request.getType() == StockMovementType.ADJUSTMENT) {
            newStock = request.getQuantity();
            movementType = "adjusted to";
        }
        
        product.setCurrentStock(newStock);
        productRepository.save(product);
        
        // Create stock movement
        StockMovement movement = StockMovement.builder()
            .productId(product.getId())
            .companyId(companyId)
            .type(request.getType())
            .quantity(request.getQuantity())
            .notes(request.getNotes())
            .performedBy(userId)
            .build();
        
        movement = stockMovementRepository.save(movement);
        
        // ========== SEND NOTIFICATIONS ==========
        
        // 1. Send stock update notification to the user who performed the action
        sendStockUpdateNotification(companyId, userId, product, oldStock, newStock, movementType);
        
        // 2. Check for low stock and send alert
        checkAndSendLowStockAlert(companyId, product);
        
        return mapToMovementResponse(movement);
    }
    
    private void checkAndSendLowStockAlert(Long companyId, Product product) {
        int currentStock = product.getCurrentStock();
        int minStock = product.getMinimumStockLevel();
        
        if (currentStock < minStock) {
            String title = currentStock == 0 ? "🔴 OUT OF STOCK" : "⚠️ LOW STOCK";
            String message = currentStock == 0 
                ? "Product '" + product.getName() + "' is OUT OF STOCK! Please restock immediately."
                : "Product '" + product.getName() + "' is running low. Current stock: " + currentStock + " units. Minimum required: " + minStock;
            
            // Send to all admins, managers, and owner
            sendNotificationToCompanyAdminsAndManagers(companyId, title, message, 
                currentStock == 0 ? "ERROR" : "WARNING", 
                "/inventory", 
                currentStock == 0 ? "error" : "warning");
        }
    }
    
    private void sendStockUpdateNotification(Long companyId, Long userId, Product product, int oldStock, int newStock, String movementType) {
        try {
            String message = "Stock for '" + product.getName() + "' has been " + movementType + " " + 
                            Math.abs(newStock - oldStock) + " units. New stock: " + newStock + " units.";
            
            NotificationRequest request = new NotificationRequest();
            request.setTitle("📦 Stock Updated");
            request.setMessage(message);
            request.setType("INVENTORY");
            request.setLink("/inventory");
            request.setIcon("inventory_2");
            
            notificationService.createNotification(userId, companyId, request);
            log.info("Stock update notification sent to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send stock update notification: {}", e.getMessage());
        }
    }
    
    private void sendProductNotification(Long companyId, String title, String message, String type, String link, String icon) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink(link);
            request.setIcon(icon);
            
            // Send to all admins and managers
            sendNotificationToCompanyAdminsAndManagers(companyId, title, message, type, link, icon);
        } catch (Exception e) {
            log.error("Failed to send product notification: {}", e.getMessage());
        }
    }
    
    private void sendNotificationToCompanyAdminsAndManagers(Long companyId, String title, String message, String type, String link, String icon) {
        try {
            List<User> admins = userRepository.findByCompanyIdAndRole(companyId, Role.ADMIN);
            List<User> managers = userRepository.findByCompanyIdAndRole(companyId, Role.MANAGER);
            User owner = userRepository.findFirstByCompanyIdAndRole(companyId, Role.OWNER).orElse(null);
            
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink(link);
            request.setIcon(icon);
            
            // Send to all admins
            for (User admin : admins) {
                notificationService.createNotification(admin.getId(), companyId, request);
            }
            
            // Send to all managers
            for (User manager : managers) {
                notificationService.createNotification(manager.getId(), companyId, request);
            }
            
            // Send to owner if exists
            if (owner != null) {
                notificationService.createNotification(owner.getId(), companyId, request);
            }
            
            log.info("Notification sent to company admins, managers, and owner for company: {}", companyId);
        } catch (Exception e) {
            log.error("Failed to send notification to company admins and managers: {}", e.getMessage());
        }
    }
    
    public List<ProductResponse> getCompanyProducts(Long companyId) {
        return productRepository.findByCompanyIdAndIsActiveTrue(companyId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long companyId, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Product not found for this company");
        }
        
        return mapToResponse(product);
    }
    
    public List<ProductResponse> getLowStockProducts(Long companyId) {
        return productRepository.findLowStockProducts(companyId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<StockMovementResponse> getProductMovements(Long companyId, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Product not found for this company");
        }
        
        return stockMovementRepository.findByProductId(productId)
            .stream()
            .map(this::mapToMovementResponse)
            .collect(Collectors.toList());
    }
    
    public List<StockMovementResponse> getAllMovements(Long companyId) {
        return stockMovementRepository.findByCompanyId(companyId)
            .stream()
            .map(this::mapToMovementResponse)
            .collect(Collectors.toList());
    }
    
    private String generateSKU(String productName, Long companyId) {
        String base = productName.toUpperCase().replaceAll("[^A-Za-z0-9]", "_");
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return base.substring(0, Math.min(10, base.length())) + "_" + uniqueId;
    }
    
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .sku(product.getSku())
            .category(product.getCategory())
            .unit(product.getUnit())
            .unitPrice(product.getUnitPrice())
            .sellingPrice(product.getSellingPrice())
            .currentStock(product.getCurrentStock())
            .minimumStockLevel(product.getMinimumStockLevel())
            .isLowStock(product.getCurrentStock() < product.getMinimumStockLevel())
            .imageUrl(product.getImageUrl())
            .isActive(product.getIsActive())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
    
    private StockMovementResponse mapToMovementResponse(StockMovement movement) {
        Product product = productRepository.findById(movement.getProductId()).orElse(null);
        User user = userRepository.findById(movement.getPerformedBy()).orElse(null);
        
        return StockMovementResponse.builder()
            .id(movement.getId())
            .productId(movement.getProductId())
            .productName(product != null ? product.getName() : null)
            .type(movement.getType())
            .quantity(movement.getQuantity())
            .referenceType(movement.getReferenceType())
            .notes(movement.getNotes())
            .performedByName(user != null ? user.getName() : null)
            .createdAt(movement.getCreatedAt())
            .build();
    }
}





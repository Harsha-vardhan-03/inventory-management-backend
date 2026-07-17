/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * package com.inventory.modules.purchase.services;
 * 
 * import com.inventory.common.enums.OrderStatus; import
 * com.inventory.common.enums.StockMovementType; import
 * com.inventory.common.exception.ResourceNotFoundException; import
 * com.inventory.modules.company.entities.Company; import
 * com.inventory.modules.company.repositories.CompanyRepository; import
 * com.inventory.modules.inventory.entities.Product; import
 * com.inventory.modules.inventory.entities.StockMovement; import
 * com.inventory.modules.inventory.repositories.ProductRepository; import
 * com.inventory.modules.inventory.repositories.StockMovementRepository; import
 * com.inventory.modules.purchase.dto.*; import
 * com.inventory.modules.purchase.entities.PurchaseRequest; import
 * com.inventory.modules.purchase.entities.PurchaseRequestItem; import
 * com.inventory.modules.purchase.repositories.PurchaseRequestItemRepository;
 * import com.inventory.modules.purchase.repositories.PurchaseRequestRepository;
 * import com.inventory.modules.supplier.entities.Supplier; import
 * com.inventory.modules.supplier.entities.SupplierProduct; import
 * com.inventory.modules.supplier.entities.SupplierStockMovement; import
 * com.inventory.modules.supplier.repositories.SupplierProductRepository; import
 * com.inventory.modules.supplier.repositories.SupplierRepository; import
 * com.inventory.modules.supplier.repositories.SupplierStockMovementRepository;
 * import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
 * import org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional;
 * 
 * import java.math.BigDecimal; import java.time.LocalDateTime; import
 * java.util.List; import java.util.Map; import java.util.stream.Collectors;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor
 * 
 * @Slf4j public class PurchaseRequestService {
 * 
 * private final PurchaseRequestRepository purchaseRequestRepository; private
 * final PurchaseRequestItemRepository itemRepository; private final
 * SupplierRepository supplierRepository; private final CompanyRepository
 * companyRepository; private final SupplierProductRepository
 * supplierProductRepository; private final SupplierStockMovementRepository
 * supplierStockMovementRepository; private final ProductRepository
 * productRepository; private final StockMovementRepository
 * stockMovementRepository;
 * 
 * @Transactional public PurchaseRequestResponse createPurchaseRequest(Long
 * companyId, Long userId, PurchaseRequestRequest request) {
 * log.info("Creating purchase request for company: {}", companyId);
 * 
 * // Validate supplier Supplier supplier =
 * supplierRepository.findById(request.getSupplierId()) .orElseThrow(() -> new
 * ResourceNotFoundException("Supplier not found"));
 * 
 * Company company = companyRepository.findById(companyId) .orElseThrow(() ->
 * new ResourceNotFoundException("Company not found"));
 * 
 * // Create purchase request PurchaseRequest purchaseRequest =
 * PurchaseRequest.builder() .companyId(companyId)
 * .supplierId(request.getSupplierId()) .status(OrderStatus.PENDING)
 * .totalAmount(BigDecimal.ZERO) .deliveryNotes(request.getDeliveryNotes())
 * .build();
 * 
 * purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
 * 
 * BigDecimal totalAmount = BigDecimal.ZERO;
 * 
 * // Create items for (PurchaseRequestItemDTO itemDTO : request.getItems()) {
 * SupplierProduct supplierProduct =
 * supplierProductRepository.findById(itemDTO.getSupplierProductId())
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Supplier product not found"));
 * 
 * // Validate supplier product belongs to supplier if
 * (!supplierProduct.getSupplierId().equals(request.getSupplierId())) { throw
 * new
 * IllegalArgumentException("Product does not belong to the specified supplier"
 * ); }
 * 
 * BigDecimal unitPrice = supplierProduct.getUnitPrice(); BigDecimal totalPrice
 * = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getRequestedQty()));
 * 
 * PurchaseRequestItem item = PurchaseRequestItem.builder()
 * .purchaseRequestId(purchaseRequest.getId())
 * .supplierProductId(itemDTO.getSupplierProductId())
 * .productName(itemDTO.getProductName() != null ? itemDTO.getProductName() :
 * supplierProduct.getName()) .requestedQty(itemDTO.getRequestedQty())
 * .unitPrice(unitPrice) .totalPrice(totalPrice) .receivedQty(0) // Initialize
 * received quantity .build();
 * 
 * itemRepository.save(item); totalAmount = totalAmount.add(totalPrice); }
 * 
 * // Update total amount purchaseRequest.setTotalAmount(totalAmount);
 * purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
 * 
 * log.info("Purchase request created with ID: {}", purchaseRequest.getId());
 * 
 * return mapToResponse(purchaseRequest); }
 * 
 * @Transactional public PurchaseRequestResponse approvePurchaseRequest(Long
 * purchaseRequestId, Long supplierId) {
 * log.info("Approving purchase request: {} by supplier: {}", purchaseRequestId,
 * supplierId);
 * 
 * PurchaseRequest request =
 * purchaseRequestRepository.findById(purchaseRequestId) .orElseThrow(() -> new
 * ResourceNotFoundException("Purchase request not found"));
 * 
 * if (!request.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Purchase request not found for this supplier"); }
 * 
 * if (request.getStatus() != OrderStatus.PENDING) { throw new
 * IllegalStateException("Purchase request is not in PENDING state. Current status: "
 * + request.getStatus()); }
 * 
 * request.setStatus(OrderStatus.APPROVED); request =
 * purchaseRequestRepository.save(request);
 * 
 * log.info("Purchase request approved: {}", purchaseRequestId);
 * 
 * return mapToResponse(request); }
 * 
 * @Transactional public PurchaseRequestResponse rejectPurchaseRequest(Long
 * purchaseRequestId, Long supplierId, ApproveRejectRequest rejectRequest) {
 * log.info("Rejecting purchase request: {} by supplier: {}", purchaseRequestId,
 * supplierId);
 * 
 * PurchaseRequest request =
 * purchaseRequestRepository.findById(purchaseRequestId) .orElseThrow(() -> new
 * ResourceNotFoundException("Purchase request not found"));
 * 
 * if (!request.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Purchase request not found for this supplier"); }
 * 
 * if (request.getStatus() != OrderStatus.PENDING) { throw new
 * IllegalStateException("Purchase request is not in PENDING state. Current status: "
 * + request.getStatus()); }
 * 
 * request.setStatus(OrderStatus.REJECTED);
 * request.setRejectionReason(rejectRequest.getRejectionReason()); request =
 * purchaseRequestRepository.save(request);
 * 
 * log.info("Purchase request rejected: {}", purchaseRequestId);
 * 
 * return mapToResponse(request); }
 * 
 * @Transactional public PurchaseRequestResponse dispatchPurchaseRequest(Long
 * purchaseRequestId, Long supplierId) {
 * log.info("Dispatching purchase request: {} by supplier: {}",
 * purchaseRequestId, supplierId);
 * 
 * PurchaseRequest request =
 * purchaseRequestRepository.findById(purchaseRequestId) .orElseThrow(() -> new
 * ResourceNotFoundException("Purchase request not found"));
 * 
 * if (!request.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Purchase request not found for this supplier"); }
 * 
 * if (request.getStatus() != OrderStatus.APPROVED) { throw new
 * IllegalStateException("Purchase request must be APPROVED before dispatching. Current status: "
 * + request.getStatus()); }
 * 
 * // Get all items List<PurchaseRequestItem> items =
 * itemRepository.findByPurchaseRequestId(purchaseRequestId);
 * 
 * // Deduct stock from supplier for each item for (PurchaseRequestItem item :
 * items) { SupplierProduct supplierProduct =
 * supplierProductRepository.findById(item.getSupplierProductId())
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Supplier product not found: " +
 * item.getSupplierProductId()));
 * 
 * // Check if supplier has enough stock int currentStock =
 * supplierProduct.getAvailableQty() != null ? supplierProduct.getAvailableQty()
 * : 0; int requestedQty = item.getRequestedQty();
 * 
 * if (currentStock < requestedQty) { throw new IllegalStateException(
 * "Insufficient stock for product: " + supplierProduct.getName() +
 * ". Available: " + currentStock + ", Requested: " + requestedQty ); }
 * 
 * // Deduct from supplier's stock int newStock = currentStock - requestedQty;
 * supplierProduct.setAvailableQty(newStock);
 * supplierProductRepository.save(supplierProduct);
 * 
 * // Create supplier stock movement (STOCK_OUT) SupplierStockMovement
 * supplierMovement = SupplierStockMovement.builder()
 * .supplierProductId(supplierProduct.getId()) .type("STOCK_OUT")
 * .quantity(requestedQty) .referenceType("PURCHASE_DISPATCH")
 * .referenceId(purchaseRequestId)
 * .notes("Stock deducted for purchase request #" + purchaseRequestId)
 * .performedBy(supplierId) .build();
 * 
 * supplierStockMovementRepository.save(supplierMovement);
 * 
 * log.info("Deducted {} units of product {} from supplier stock. New stock: {}"
 * , requestedQty, supplierProduct.getName(), newStock); }
 * 
 * // Update request status request.setStatus(OrderStatus.DISPATCHED);
 * request.setDispatchedAt(LocalDateTime.now()); request =
 * purchaseRequestRepository.save(request);
 * 
 * log.info("Purchase request dispatched: {}", purchaseRequestId);
 * 
 * return mapToResponse(request); }
 * 
 * @Transactional public PurchaseRequestResponse deliverPurchaseRequest(Long
 * purchaseRequestId, Long companyId, Long userId, DeliveryRequest
 * deliveryRequest) {
 * log.info("Delivering purchase request: {} by company: {}, user: {}",
 * purchaseRequestId, companyId, userId);
 * 
 * PurchaseRequest request =
 * purchaseRequestRepository.findById(purchaseRequestId) .orElseThrow(() -> new
 * ResourceNotFoundException("Purchase request not found with ID: " +
 * purchaseRequestId));
 * 
 * if (!request.getCompanyId().equals(companyId)) { throw new
 * ResourceNotFoundException("Purchase request not found for this company"); }
 * 
 * if (request.getStatus() != OrderStatus.DISPATCHED) { throw new
 * IllegalStateException("Purchase request must be DISPATCHED before delivery. Current status: "
 * + request.getStatus()); }
 * 
 * // Get all items List<PurchaseRequestItem> items =
 * itemRepository.findByPurchaseRequestId(purchaseRequestId);
 * 
 * if (items.isEmpty()) { throw new
 * IllegalStateException("No items found for this purchase request"); }
 * 
 * log.info("Processing {} items for delivery", items.size());
 * 
 * // Get received quantities from request Map<Long, Integer> receivedQuantities
 * = deliveryRequest.getReceivedQuantities();
 * 
 * if (receivedQuantities == null || receivedQuantities.isEmpty()) { throw new
 * IllegalArgumentException("Received quantities are required"); }
 * 
 * // Process each item for (PurchaseRequestItem item : items) { Integer
 * receivedQty = receivedQuantities.get(item.getId());
 * 
 * if (receivedQty == null) { log.
 * warn("No received quantity specified for item: {}, using requested quantity",
 * item.getId()); receivedQty = item.getRequestedQty(); // Default to full
 * quantity }
 * 
 * if (receivedQty < 0) { throw new
 * IllegalArgumentException("Received quantity cannot be negative for item: " +
 * item.getId()); }
 * 
 * if (receivedQty > item.getRequestedQty()) { throw new
 * IllegalArgumentException( "Received quantity (" + receivedQty +
 * ") cannot exceed requested quantity (" + item.getRequestedQty() +
 * ") for product: " + item.getProductName() ); }
 * 
 * log.info("Processing item: {}, Product: {}, Requested: {}, Received: {}",
 * item.getId(), item.getProductName(), item.getRequestedQty(), receivedQty);
 * 
 * // Update received quantity item.setReceivedQty(receivedQty);
 * itemRepository.save(item);
 * 
 * // ONLY add to company inventory if received quantity > 0 if (receivedQty >
 * 0) { addToCompanyInventory(companyId, item, receivedQty, userId,
 * purchaseRequestId); log.info("Added {} units of {} to company inventory",
 * receivedQty, item.getProductName()); } else {
 * log.warn("Received quantity is 0 for product: {}, skipping inventory update",
 * item.getProductName()); } }
 * 
 * // Update request status request.setStatus(OrderStatus.DELIVERED);
 * request.setDeliveredAt(LocalDateTime.now()); request =
 * purchaseRequestRepository.save(request);
 * 
 * log.info("Purchase request delivered successfully: {}", purchaseRequestId);
 * 
 * return mapToResponse(request); }
 * 
 *//**
	 * Add products to company inventory
	 */
/*
 * private void addToCompanyInventory(Long companyId, PurchaseRequestItem item,
 * Integer receivedQty, Long userId, Long purchaseRequestId) {
 * log.info("Adding to company inventory: companyId={}, product={}, quantity={}"
 * , companyId, item.getProductName(), receivedQty);
 * 
 * // Find existing product in company's inventory by name Product product =
 * productRepository.findByCompanyIdAndName(companyId, item.getProductName())
 * .orElseGet(() -> {
 * log.info("Product not found in inventory, creating new product: {}",
 * item.getProductName()); // Create new product if not exists Product
 * newProduct = Product.builder() .companyId(companyId)
 * .name(item.getProductName()) .sku(generateSKU(item.getProductName()))
 * .unit("PCS") // Default unit .unitPrice(item.getUnitPrice())
 * .sellingPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(1.2))) // 20%
 * markup .currentStock(0) .minimumStockLevel(5) .isActive(true) .build();
 * return productRepository.save(newProduct); });
 * 
 * // Update stock int oldStock = product.getCurrentStock() != null ?
 * product.getCurrentStock() : 0; int newStock = oldStock + receivedQty;
 * product.setCurrentStock(newStock); productRepository.save(product);
 * 
 * log.info("Stock updated: product={}, oldStock={}, newStock={}",
 * product.getName(), oldStock, newStock);
 * 
 * // Create stock movement (STOCK_IN) StockMovement movement =
 * StockMovement.builder() .productId(product.getId()) .companyId(companyId)
 * .type(StockMovementType.STOCK_IN) .quantity(receivedQty)
 * .referenceId(purchaseRequestId) .referenceType("PURCHASE_REQUEST")
 * .notes("Stock received from purchase order #" + purchaseRequestId + " - " +
 * item.getProductName()) .performedBy(userId) .build();
 * 
 * stockMovementRepository.save(movement);
 * 
 * log.info("Stock movement created: productId={}, type=STOCK_IN, quantity={}",
 * product.getId(), receivedQty); }
 * 
 *//**
	 * Generate SKU for product
	 *//*
		 * private String generateSKU(String productName) { String base =
		 * productName.toUpperCase().replaceAll("[^A-Za-z0-9]", "_"); String timestamp =
		 * String.valueOf(System.currentTimeMillis()).substring(8); return
		 * base.substring(0, Math.min(8, base.length())) + "_" + timestamp; }
		 * 
		 * public List<PurchaseRequestResponse> getCompanyPurchaseRequests(Long
		 * companyId) { log.info("Getting purchase requests for company: {}",
		 * companyId); return
		 * purchaseRequestRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
		 * .stream() .map(this::mapToResponse) .collect(Collectors.toList()); }
		 * 
		 * public List<PurchaseRequestResponse> getSupplierPurchaseRequests(Long
		 * supplierId) { log.info("Getting purchase requests for supplier: {}",
		 * supplierId); return
		 * purchaseRequestRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId)
		 * .stream() .map(this::mapToResponse) .collect(Collectors.toList()); }
		 * 
		 * public PurchaseRequestResponse getPurchaseRequestById(Long id, Long
		 * companyId) { PurchaseRequest request = purchaseRequestRepository.findById(id)
		 * .orElseThrow(() -> new
		 * ResourceNotFoundException("Purchase request not found"));
		 * 
		 * if (!request.getCompanyId().equals(companyId)) { throw new
		 * ResourceNotFoundException("Purchase request not found for this company"); }
		 * 
		 * return mapToResponse(request); }
		 * 
		 * private PurchaseRequestResponse mapToResponse(PurchaseRequest request) {
		 * Supplier supplier =
		 * supplierRepository.findById(request.getSupplierId()).orElse(null); Company
		 * company = companyRepository.findById(request.getCompanyId()).orElse(null);
		 * 
		 * List<PurchaseRequestItem> items =
		 * itemRepository.findByPurchaseRequestId(request.getId());
		 * List<PurchaseRequestItemDTO> itemDTOs = items.stream() .map(item -> {
		 * PurchaseRequestItemDTO dto = new PurchaseRequestItemDTO();
		 * dto.setId(item.getId());
		 * dto.setSupplierProductId(item.getSupplierProductId());
		 * dto.setProductName(item.getProductName());
		 * dto.setRequestedQty(item.getRequestedQty());
		 * dto.setUnitPrice(item.getUnitPrice());
		 * dto.setReceivedQty(item.getReceivedQty() != null ? item.getReceivedQty() :
		 * 0); return dto; }) .collect(Collectors.toList());
		 * 
		 * return PurchaseRequestResponse.builder() .id(request.getId())
		 * .companyId(request.getCompanyId()) .companyName(company != null ?
		 * company.getName() : null) .supplierId(request.getSupplierId())
		 * .supplierName(supplier != null ? supplier.getCompanyName() : null)
		 * .status(request.getStatus()) .totalAmount(request.getTotalAmount())
		 * .rejectionReason(request.getRejectionReason())
		 * .deliveryNotes(request.getDeliveryNotes())
		 * .dispatchedAt(request.getDispatchedAt())
		 * .deliveredAt(request.getDeliveredAt()) .createdAt(request.getCreatedAt())
		 * .updatedAt(request.getUpdatedAt()) .items(itemDTOs) .build(); } }
		 */













package com.inventory.modules.purchase.services;

import com.inventory.common.enums.OrderStatus;
import com.inventory.common.enums.Role;
import com.inventory.common.enums.StockMovementType;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.company.entities.Company;
import com.inventory.modules.company.repositories.CompanyRepository;
import com.inventory.modules.inventory.entities.Product;
import com.inventory.modules.inventory.entities.StockMovement;
import com.inventory.modules.inventory.repositories.ProductRepository;
import com.inventory.modules.inventory.repositories.StockMovementRepository;
import com.inventory.modules.notification.dto.NotificationRequest;
import com.inventory.modules.notification.services.NotificationService;
import com.inventory.modules.purchase.dto.*;
import com.inventory.modules.purchase.entities.PurchaseRequest;
import com.inventory.modules.purchase.entities.PurchaseRequestItem;
import com.inventory.modules.purchase.repositories.PurchaseRequestItemRepository;
import com.inventory.modules.purchase.repositories.PurchaseRequestRepository;
import com.inventory.modules.supplier.entities.Supplier;
import com.inventory.modules.supplier.entities.SupplierProduct;
import com.inventory.modules.supplier.entities.SupplierStockMovement;
import com.inventory.modules.supplier.repositories.SupplierProductRepository;
import com.inventory.modules.supplier.repositories.SupplierRepository;
import com.inventory.modules.supplier.repositories.SupplierStockMovementRepository;
import com.inventory.modules.user.entities.User;
import com.inventory.modules.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseRequestService {
    
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;
    private final SupplierProductRepository supplierProductRepository;
    private final SupplierStockMovementRepository supplierStockMovementRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationService notificationService;  // ADD THIS
    private final UserRepository userRepository;  // ADD THIS
    
    @Transactional
    public PurchaseRequestResponse createPurchaseRequest(Long companyId, Long userId, PurchaseRequestRequest request) {
        log.info("Creating purchase request for company: {}", companyId);
        
        // Validate supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        // Create purchase request
        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
            .companyId(companyId)
            .supplierId(request.getSupplierId())
            .status(OrderStatus.PENDING)
            .totalAmount(BigDecimal.ZERO)
            .deliveryNotes(request.getDeliveryNotes())
            .build();
        
        purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder productNames = new StringBuilder();
        
        // Create items
        for (PurchaseRequestItemDTO itemDTO : request.getItems()) {
            SupplierProduct supplierProduct = supplierProductRepository.findById(itemDTO.getSupplierProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product not found"));
            
            // Validate supplier product belongs to supplier
            if (!supplierProduct.getSupplierId().equals(request.getSupplierId())) {
                throw new IllegalArgumentException("Product does not belong to the specified supplier");
            }
            
            BigDecimal unitPrice = supplierProduct.getUnitPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getRequestedQty()));
            
            PurchaseRequestItem item = PurchaseRequestItem.builder()
                .purchaseRequestId(purchaseRequest.getId())
                .supplierProductId(itemDTO.getSupplierProductId())
                .productName(itemDTO.getProductName() != null ? itemDTO.getProductName() : supplierProduct.getName())
                .requestedQty(itemDTO.getRequestedQty())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .receivedQty(0)
                .build();
            
            itemRepository.save(item);
            totalAmount = totalAmount.add(totalPrice);
            
            if (productNames.length() > 0) {
                productNames.append(", ");
            }
            productNames.append(supplierProduct.getName()).append(" (x").append(itemDTO.getRequestedQty()).append(")");
        }
        
        // Update total amount
        purchaseRequest.setTotalAmount(totalAmount);
        purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
        
        log.info("Purchase request created with ID: {}", purchaseRequest.getId());
        
        // ========== SEND NOTIFICATIONS ==========
        
        // 1. Notify the company user who created the request
        sendNotificationToUser(userId, companyId, 
            "📦 Purchase Request Created", 
            "Your purchase request #" + purchaseRequest.getId() + " has been created successfully. " +
            "Products: " + productNames.toString() + ". Total: ₹" + totalAmount,
            "PURCHASE", "/purchase-requests/" + purchaseRequest.getId(), "shopping_cart");
        
        // 2. Notify the supplier
        sendNotificationToSupplier(supplier.getUserId(), 
            "📋 New Purchase Request", 
            "Company " + company.getName() + " has sent a purchase request #" + purchaseRequest.getId() + ". " +
            "Products: " + productNames.toString() + ". Total: ₹" + totalAmount,
            "PURCHASE", "/supplier/purchase-requests", "shopping_cart");
        
        return mapToResponse(purchaseRequest);
    }
    
    @Transactional
    public PurchaseRequestResponse approvePurchaseRequest(Long purchaseRequestId, Long supplierId) {
        log.info("Approving purchase request: {} by supplier: {}", purchaseRequestId, supplierId);
        
        PurchaseRequest request = purchaseRequestRepository.findById(purchaseRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found"));
        
        if (!request.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Purchase request not found for this supplier");
        }
        
        if (request.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Purchase request is not in PENDING state. Current status: " + request.getStatus());
        }
        
        request.setStatus(OrderStatus.APPROVED);
        request = purchaseRequestRepository.save(request);
        
        log.info("Purchase request approved: {}", purchaseRequestId);
        
        // ========== SEND NOTIFICATION ==========
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        
        // Notify the company that the request was approved
        sendNotificationToCompanyAdmins(request.getCompanyId(),
            "✅ Purchase Request Approved",
            "Supplier " + (supplier != null ? supplier.getCompanyName() : "Supplier") + 
            " has approved your purchase request #" + purchaseRequestId + ".",
            "SUCCESS", "/purchase-requests/" + purchaseRequestId, "check_circle");
        
        return mapToResponse(request);
    }
    
    @Transactional
    public PurchaseRequestResponse rejectPurchaseRequest(Long purchaseRequestId, Long supplierId, ApproveRejectRequest rejectRequest) {
        log.info("Rejecting purchase request: {} by supplier: {}", purchaseRequestId, supplierId);
        
        PurchaseRequest request = purchaseRequestRepository.findById(purchaseRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found"));
        
        if (!request.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Purchase request not found for this supplier");
        }
        
        if (request.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Purchase request is not in PENDING state. Current status: " + request.getStatus());
        }
        
        request.setStatus(OrderStatus.REJECTED);
        request.setRejectionReason(rejectRequest.getRejectionReason());
        request = purchaseRequestRepository.save(request);
        
        log.info("Purchase request rejected: {}", purchaseRequestId);
        
        // ========== SEND NOTIFICATION ==========
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        
        // Notify the company that the request was rejected
        sendNotificationToCompanyAdmins(request.getCompanyId(),
            "❌ Purchase Request Rejected",
            "Supplier " + (supplier != null ? supplier.getCompanyName() : "Supplier") + 
            " has rejected your purchase request #" + purchaseRequestId + 
            ". Reason: " + rejectRequest.getRejectionReason(),
            "ERROR", "/purchase-requests/" + purchaseRequestId, "cancel");
        
        return mapToResponse(request);
    }
    
    @Transactional
    public PurchaseRequestResponse dispatchPurchaseRequest(Long purchaseRequestId, Long supplierId) {
        log.info("Dispatching purchase request: {} by supplier: {}", purchaseRequestId, supplierId);
        
        PurchaseRequest request = purchaseRequestRepository.findById(purchaseRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found"));
        
        if (!request.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Purchase request not found for this supplier");
        }
        
        if (request.getStatus() != OrderStatus.APPROVED) {
            throw new IllegalStateException("Purchase request must be APPROVED before dispatching. Current status: " + request.getStatus());
        }
        
        // Get all items
        List<PurchaseRequestItem> items = itemRepository.findByPurchaseRequestId(purchaseRequestId);
        StringBuilder productNames = new StringBuilder();
        
        // Deduct stock from supplier for each item
        for (PurchaseRequestItem item : items) {
            SupplierProduct supplierProduct = supplierProductRepository.findById(item.getSupplierProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product not found: " + item.getSupplierProductId()));
            
            // Check if supplier has enough stock
            int currentStock = supplierProduct.getAvailableQty() != null ? supplierProduct.getAvailableQty() : 0;
            int requestedQty = item.getRequestedQty();
            
            if (currentStock < requestedQty) {
                throw new IllegalStateException(
                    "Insufficient stock for product: " + supplierProduct.getName() + 
                    ". Available: " + currentStock + ", Requested: " + requestedQty
                );
            }
            
            // Deduct from supplier's stock
            int newStock = currentStock - requestedQty;
            supplierProduct.setAvailableQty(newStock);
            supplierProductRepository.save(supplierProduct);
            
            // Create supplier stock movement (STOCK_OUT)
            SupplierStockMovement supplierMovement = SupplierStockMovement.builder()
                .supplierProductId(supplierProduct.getId())
                .type("STOCK_OUT")
                .quantity(requestedQty)
                .referenceType("PURCHASE_DISPATCH")
                .referenceId(purchaseRequestId)
                .notes("Stock deducted for purchase request #" + purchaseRequestId)
                .performedBy(supplierId)
                .build();
            
            supplierStockMovementRepository.save(supplierMovement);
            
            if (productNames.length() > 0) {
                productNames.append(", ");
            }
            productNames.append(supplierProduct.getName()).append(" (x").append(requestedQty).append(")");
            
            log.info("Deducted {} units of product {} from supplier stock. New stock: {}", 
                requestedQty, supplierProduct.getName(), newStock);
        }
        
        // Update request status
        request.setStatus(OrderStatus.DISPATCHED);
        request.setDispatchedAt(LocalDateTime.now());
        request = purchaseRequestRepository.save(request);
        
        log.info("Purchase request dispatched: {}", purchaseRequestId);
        
        // ========== SEND NOTIFICATION ==========
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        
        // Notify the company that the order was dispatched
        sendNotificationToCompanyAdmins(request.getCompanyId(),
            "🚚 Order Dispatched",
            "Supplier " + (supplier != null ? supplier.getCompanyName() : "Supplier") + 
            " has dispatched your purchase request #" + purchaseRequestId + ". " +
            "Products: " + productNames.toString(),
            "PURCHASE", "/purchase-requests/" + purchaseRequestId, "local_shipping");
        
        // Also notify managers and workers to expect delivery
        sendNotificationToCompanyManagersAndWorkers(request.getCompanyId(),
            "📦 Order Ready for Delivery",
            "Order #" + purchaseRequestId + " has been dispatched by " + 
            (supplier != null ? supplier.getCompanyName() : "Supplier") + 
            ". Please confirm receipt when delivered.",
            "PURCHASE", "/purchase-requests/" + purchaseRequestId, "local_shipping");
        
        return mapToResponse(request);
    }
    
    @Transactional
    public PurchaseRequestResponse deliverPurchaseRequest(Long purchaseRequestId, Long companyId, Long userId, DeliveryRequest deliveryRequest) {
        log.info("Delivering purchase request: {} by company: {}, user: {}", purchaseRequestId, companyId, userId);
        
        PurchaseRequest request = purchaseRequestRepository.findById(purchaseRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found with ID: " + purchaseRequestId));
        
        if (!request.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Purchase request not found for this company");
        }
        
        if (request.getStatus() != OrderStatus.DISPATCHED) {
            throw new IllegalStateException("Purchase request must be DISPATCHED before delivery. Current status: " + request.getStatus());
        }
        
        // Get all items
        List<PurchaseRequestItem> items = itemRepository.findByPurchaseRequestId(purchaseRequestId);
        
        if (items.isEmpty()) {
            throw new IllegalStateException("No items found for this purchase request");
        }
        
        log.info("Processing {} items for delivery", items.size());
        
        // Get received quantities from request
        Map<Long, Integer> receivedQuantities = deliveryRequest.getReceivedQuantities();
        
        if (receivedQuantities == null || receivedQuantities.isEmpty()) {
            throw new IllegalArgumentException("Received quantities are required");
        }
        
        StringBuilder productNames = new StringBuilder();
        
        // Process each item
        for (PurchaseRequestItem item : items) {
            Integer receivedQty = receivedQuantities.get(item.getId());
            
            if (receivedQty == null) {
                log.warn("No received quantity specified for item: {}, using requested quantity", item.getId());
                receivedQty = item.getRequestedQty();
            }
            
            if (receivedQty < 0) {
                throw new IllegalArgumentException("Received quantity cannot be negative for item: " + item.getId());
            }
            
            if (receivedQty > item.getRequestedQty()) {
                throw new IllegalArgumentException(
                    "Received quantity (" + receivedQty + ") cannot exceed requested quantity (" + 
                    item.getRequestedQty() + ") for product: " + item.getProductName()
                );
            }
            
            log.info("Processing item: {}, Product: {}, Requested: {}, Received: {}", 
                item.getId(), item.getProductName(), item.getRequestedQty(), receivedQty);
            
            // Update received quantity
            item.setReceivedQty(receivedQty);
            itemRepository.save(item);
            
            // ONLY add to company inventory if received quantity > 0
            if (receivedQty > 0) {
                addToCompanyInventory(companyId, item, receivedQty, userId, purchaseRequestId);
                
                if (productNames.length() > 0) {
                    productNames.append(", ");
                }
                productNames.append(item.getProductName()).append(" (x").append(receivedQty).append(")");
                
                log.info("Added {} units of {} to company inventory", receivedQty, item.getProductName());
            } else {
                log.warn("Received quantity is 0 for product: {}, skipping inventory update", item.getProductName());
            }
        }
        
        // Update request status
        request.setStatus(OrderStatus.DELIVERED);
        request.setDeliveredAt(LocalDateTime.now());
        request = purchaseRequestRepository.save(request);
        
        log.info("Purchase request delivered successfully: {}", purchaseRequestId);
        
        // ========== SEND NOTIFICATIONS ==========
        
        // Notify the user who delivered
        sendNotificationToUser(userId, companyId,
            "📦 Order Delivered Successfully",
            "You have successfully delivered purchase request #" + purchaseRequestId + ". " +
            "Products: " + productNames.toString(),
            "SUCCESS", "/purchase-requests/" + purchaseRequestId, "check_circle");
        
        // Notify all admins and owner about delivery
        sendNotificationToCompanyAdmins(companyId,
            "📦 Order Delivered",
            "Purchase request #" + purchaseRequestId + " has been delivered. " +
            "Products: " + productNames.toString() + ". Stock has been updated.",
            "SUCCESS", "/purchase-requests/" + purchaseRequestId, "check_circle");
        
        return mapToResponse(request);
    }
    
    /**
     * Add products to company inventory
     */
    private void addToCompanyInventory(Long companyId, PurchaseRequestItem item, Integer receivedQty, Long userId, Long purchaseRequestId) {
        log.info("Adding to company inventory: companyId={}, product={}, quantity={}", 
            companyId, item.getProductName(), receivedQty);
        
        // Find existing product in company's inventory by name
        Product product = productRepository.findByCompanyIdAndName(companyId, item.getProductName())
            .orElseGet(() -> {
                log.info("Product not found in inventory, creating new product: {}", item.getProductName());
                Product newProduct = Product.builder()
                    .companyId(companyId)
                    .name(item.getProductName())
                    .sku(generateSKU(item.getProductName()))
                    .unit("PCS")
                    .unitPrice(item.getUnitPrice())
                    .sellingPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(1.2)))
                    .currentStock(0)
                    .minimumStockLevel(5)
                    .isActive(true)
                    .build();
                return productRepository.save(newProduct);
            });
        
        // Update stock
        int oldStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
        int newStock = oldStock + receivedQty;
        product.setCurrentStock(newStock);
        productRepository.save(product);
        
        log.info("Stock updated: product={}, oldStock={}, newStock={}", 
            product.getName(), oldStock, newStock);
        
        // Create stock movement (STOCK_IN)
        StockMovement movement = StockMovement.builder()
            .productId(product.getId())
            .companyId(companyId)
            .type(StockMovementType.STOCK_IN)
            .quantity(receivedQty)
            .referenceId(purchaseRequestId)
            .referenceType("PURCHASE_REQUEST")
            .notes("Stock received from purchase order #" + purchaseRequestId + " - " + item.getProductName())
            .performedBy(userId)
            .build();
        
        stockMovementRepository.save(movement);
        
        log.info("Stock movement created: productId={}, type=STOCK_IN, quantity={}", 
            product.getId(), receivedQty);
    }
    
    // ========== NOTIFICATION HELPER METHODS ==========
    
    private void sendNotificationToUser(Long userId, Long companyId, String title, String message, String type, String link, String icon) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink(link);
            request.setIcon(icon);
            
            notificationService.createNotification(userId, companyId, request);
            log.info("Notification sent to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
    
    private void sendNotificationToSupplier(Long userId, String title, String message, String type, String link, String icon) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink(link);
            request.setIcon(icon);
            
            notificationService.createNotification(userId, null, request);
            log.info("Notification sent to supplier: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send notification to supplier {}: {}", userId, e.getMessage());
        }
    }
    
    private void sendNotificationToCompanyAdmins(Long companyId, String title, String message, String type, String link, String icon) {
        try {
            List<User> admins = userRepository.findByCompanyIdAndRole(companyId, Role.ADMIN);
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
            
            // Send to owner if exists
            if (owner != null) {
                notificationService.createNotification(owner.getId(), companyId, request);
            }
            
            log.info("Notification sent to company admins and owner for company: {}", companyId);
        } catch (Exception e) {
            log.error("Failed to send notification to company admins: {}", e.getMessage());
        }
    }
    
    private void sendNotificationToCompanyManagersAndWorkers(Long companyId, String title, String message, String type, String link, String icon) {
        try {
            List<User> managers = userRepository.findByCompanyIdAndRole(companyId, Role.MANAGER);
            List<User> workers = userRepository.findByCompanyIdAndRole(companyId, Role.WORKER);
            
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setMessage(message);
            request.setType(type);
            request.setLink(link);
            request.setIcon(icon);
            
            // Send to all managers
            for (User manager : managers) {
                notificationService.createNotification(manager.getId(), companyId, request);
            }
            
            // Send to all workers
            for (User worker : workers) {
                notificationService.createNotification(worker.getId(), companyId, request);
            }
            
            log.info("Notification sent to managers and workers for company: {}", companyId);
        } catch (Exception e) {
            log.error("Failed to send notification to managers and workers: {}", e.getMessage());
        }
    }
    
    private String generateSKU(String productName) {
        String base = productName.toUpperCase().replaceAll("[^A-Za-z0-9]", "_");
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return base.substring(0, Math.min(8, base.length())) + "_" + timestamp;
    }
    
    public List<PurchaseRequestResponse> getCompanyPurchaseRequests(Long companyId) {
        log.info("Getting purchase requests for company: {}", companyId);
        return purchaseRequestRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<PurchaseRequestResponse> getSupplierPurchaseRequests(Long supplierId) {
        log.info("Getting purchase requests for supplier: {}", supplierId);
        return purchaseRequestRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public PurchaseRequestResponse getPurchaseRequestById(Long id, Long companyId) {
        PurchaseRequest request = purchaseRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found"));
        
        if (!request.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Purchase request not found for this company");
        }
        
        return mapToResponse(request);
    }
    
    private PurchaseRequestResponse mapToResponse(PurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
        Company company = companyRepository.findById(request.getCompanyId()).orElse(null);
        
        List<PurchaseRequestItem> items = itemRepository.findByPurchaseRequestId(request.getId());
        List<PurchaseRequestItemDTO> itemDTOs = items.stream()
            .map(item -> {
                PurchaseRequestItemDTO dto = new PurchaseRequestItemDTO();
                dto.setId(item.getId());
                dto.setSupplierProductId(item.getSupplierProductId());
                dto.setProductName(item.getProductName());
                dto.setRequestedQty(item.getRequestedQty());
                dto.setUnitPrice(item.getUnitPrice());
                dto.setReceivedQty(item.getReceivedQty() != null ? item.getReceivedQty() : 0);
                return dto;
            })
            .collect(Collectors.toList());
        
        return PurchaseRequestResponse.builder()
            .id(request.getId())
            .companyId(request.getCompanyId())
            .companyName(company != null ? company.getName() : null)
            .supplierId(request.getSupplierId())
            .supplierName(supplier != null ? supplier.getCompanyName() : null)
            .status(request.getStatus())
            .totalAmount(request.getTotalAmount())
            .rejectionReason(request.getRejectionReason())
            .deliveryNotes(request.getDeliveryNotes())
            .dispatchedAt(request.getDispatchedAt())
            .deliveredAt(request.getDeliveredAt())
            .createdAt(request.getCreatedAt())
            .updatedAt(request.getUpdatedAt())
            .items(itemDTOs)
            .build();
    }
}
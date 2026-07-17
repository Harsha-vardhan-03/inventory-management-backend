/*
 * package com.inventory.modules.supplier.services;
 * 
 * import com.inventory.common.enums.LinkStatus; import
 * com.inventory.common.exception.DuplicateResourceException; import
 * com.inventory.common.exception.ResourceNotFoundException; import
 * com.inventory.modules.company.entities.Company; import
 * com.inventory.modules.company.repositories.CompanyRepository; import
 * com.inventory.modules.purchase.dto.PurchaseRequestResponse; import
 * com.inventory.modules.purchase.entities.PurchaseRequest; import
 * com.inventory.modules.purchase.repositories.PurchaseRequestRepository; import
 * com.inventory.modules.supplier.dto.*; import
 * com.inventory.modules.supplier.entities.CompanySupplierLink; import
 * com.inventory.modules.supplier.entities.Supplier; import
 * com.inventory.modules.supplier.entities.SupplierProduct; import
 * com.inventory.modules.supplier.repositories.CompanySupplierLinkRepository;
 * import com.inventory.modules.supplier.repositories.SupplierProductRepository;
 * import com.inventory.modules.supplier.repositories.SupplierRepository; import
 * com.inventory.modules.user.entities.User; import
 * com.inventory.modules.user.repositories.UserRepository; import
 * lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import
 * org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional; import
 * java.util.List; import java.util.stream.Collectors;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor
 * 
 * @Slf4j public class SupplierService {
 * 
 * private final SupplierRepository supplierRepository; private final
 * SupplierProductRepository productRepository; private final
 * CompanySupplierLinkRepository linkRepository; private final CompanyRepository
 * companyRepository; private final UserRepository userRepository; private final
 * PurchaseRequestRepository purchaseRequestRepository; // Added this
 * 
 * // Get supplier by user ID public SupplierResponse getSupplierByUserId(Long
 * userId) { log.info("Getting supplier profile for user ID: {}", userId);
 * Supplier supplier = supplierRepository.findByUserId(userId) .orElseThrow(()
 * -> new ResourceNotFoundException("Supplier profile not found for user ID: " +
 * userId)); return mapToSupplierResponse(supplier); }
 * 
 * // Update supplier profile
 * 
 * @Transactional public SupplierResponse updateSupplier(Long userId,
 * SupplierRequest request) {
 * log.info("Updating supplier profile for user ID: {}", userId);
 * 
 * Supplier supplier = supplierRepository.findByUserId(userId) .orElseThrow(()
 * -> new ResourceNotFoundException("Supplier not found for user ID: " +
 * userId));
 * 
 * supplier.setCompanyName(request.getSupplierName());
 * supplier.setEmail(request.getEmail()); supplier.setPhone(request.getPhone());
 * supplier.setAddress(request.getAddress()); if (request.getGstNumber() !=
 * null) supplier.setGstNumber(request.getGstNumber()); if (request.getWebsite()
 * != null) supplier.setWebsite(request.getWebsite()); if
 * (request.getContactPerson() != null)
 * supplier.setContactPerson(request.getContactPerson()); if
 * (request.getContactPersonPhone() != null)
 * supplier.setContactPersonPhone(request.getContactPersonPhone());
 * 
 * supplier = supplierRepository.save(supplier);
 * log.info("Supplier profile updated successfully for ID: {}",
 * supplier.getId());
 * 
 * // Also update user name User user = userRepository.findById(userId)
 * .orElseThrow(() -> new ResourceNotFoundException("User not found"));
 * user.setName(request.getSupplierName()); userRepository.save(user);
 * 
 * return mapToSupplierResponse(supplier); }
 * 
 * // Supplier Product Management
 * 
 * @Transactional public SupplierProductResponse addProduct(Long supplierId,
 * SupplierProductRequest request) {
 * log.info("Adding product for supplier ID: {}", supplierId);
 * 
 * Supplier supplier = supplierRepository.findById(supplierId) .orElseThrow(()
 * -> new ResourceNotFoundException("Supplier not found with ID: " +
 * supplierId));
 * 
 * SupplierProduct product = SupplierProduct.builder() .supplierId(supplierId)
 * .name(request.getName()) .description(request.getDescription())
 * .unit(request.getUnit()) .unitPrice(request.getUnitPrice())
 * .availableQty(request.getAvailableQty()) .category(request.getCategory())
 * .build();
 * 
 * product = productRepository.save(product);
 * log.info("Product added successfully with ID: {}", product.getId());
 * 
 * return mapToProductResponse(product); }
 * 
 * @Transactional public SupplierProductResponse updateProduct(Long supplierId,
 * Long productId, SupplierProductRequest request) {
 * log.info("Updating product ID: {} for supplier ID: {}", productId,
 * supplierId);
 * 
 * SupplierProduct product = productRepository.findById(productId)
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Product not found with ID: " + productId));
 * 
 * if (!product.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Product not found for this supplier"); }
 * 
 * product.setName(request.getName());
 * product.setDescription(request.getDescription());
 * product.setUnit(request.getUnit());
 * product.setUnitPrice(request.getUnitPrice());
 * product.setAvailableQty(request.getAvailableQty());
 * product.setCategory(request.getCategory());
 * 
 * product = productRepository.save(product);
 * log.info("Product updated successfully with ID: {}", product.getId());
 * 
 * return mapToProductResponse(product); }
 * 
 * @Transactional public void deleteProduct(Long supplierId, Long productId) {
 * log.info("Deleting product ID: {} for supplier ID: {}", productId,
 * supplierId);
 * 
 * SupplierProduct product = productRepository.findById(productId)
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Product not found with ID: " + productId));
 * 
 * if (!product.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Product not found for this supplier"); }
 * 
 * productRepository.delete(product); log.info("Product deleted successfully");
 * }
 * 
 * public List<SupplierProductResponse> getSupplierProducts(Long supplierId) {
 * log.info("Getting all products for supplier ID: {}", supplierId);
 * 
 * return productRepository.findBySupplierId(supplierId) .stream()
 * .map(this::mapToProductResponse) .collect(Collectors.toList()); }
 * 
 * public SupplierProductResponse getProductById(Long supplierId, Long
 * productId) { log.info("Getting product ID: {} for supplier ID: {}",
 * productId, supplierId);
 * 
 * SupplierProduct product = productRepository.findById(productId)
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Product not found with ID: " + productId));
 * 
 * if (!product.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Product not found for this supplier"); }
 * 
 * return mapToProductResponse(product); }
 * 
 * // Company-Supplier Connection Management
 * 
 * @Transactional public CompanySupplierLinkResponse sendConnectionRequest(Long
 * companyId, CompanySupplierLinkRequest request) {
 * log.info("Sending connection request from company ID: {} to supplier ID: {}",
 * companyId, request.getSupplierId());
 * 
 * Supplier supplier = supplierRepository.findById(request.getSupplierId())
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Supplier not found with ID: " +
 * request.getSupplierId()));
 * 
 * Company company = companyRepository.findById(companyId) .orElseThrow(() ->
 * new ResourceNotFoundException("Company not found with ID: " + companyId));
 * 
 * // Check if connection already exists if
 * (linkRepository.findByCompanyIdAndSupplierId(companyId,
 * request.getSupplierId()).isPresent()) { throw new
 * DuplicateResourceException("Connection request already sent to this supplier"
 * ); }
 * 
 * CompanySupplierLink link = CompanySupplierLink.builder()
 * .companyId(companyId) .supplierId(request.getSupplierId())
 * .status(LinkStatus.PENDING) .build();
 * 
 * link = linkRepository.save(link);
 * log.info("Connection request sent successfully with link ID: {}",
 * link.getId());
 * 
 * return mapToLinkResponse(link, company.getName(), supplier.getCompanyName());
 * }
 * 
 * @Transactional public CompanySupplierLinkResponse updateConnectionStatus(Long
 * supplierId, Long linkId, UpdateLinkStatusRequest request) {
 * log.info("Updating connection status for link ID: {} to status: {}", linkId,
 * request.getStatus());
 * 
 * CompanySupplierLink link = linkRepository.findById(linkId) .orElseThrow(() ->
 * new ResourceNotFoundException("Connection not found with ID: " + linkId));
 * 
 * if (!link.getSupplierId().equals(supplierId)) { throw new
 * ResourceNotFoundException("Connection not found for this supplier"); }
 * 
 * link.setStatus(request.getStatus()); link = linkRepository.save(link);
 * 
 * Company company = companyRepository.findById(link.getCompanyId())
 * .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
 * Supplier supplier = supplierRepository.findById(supplierId) .orElseThrow(()
 * -> new ResourceNotFoundException("Supplier not found"));
 * 
 * log.info("Connection status updated successfully");
 * 
 * return mapToLinkResponse(link, company.getName(), supplier.getCompanyName());
 * }
 * 
 * public List<CompanySupplierLinkResponse> getCompanyConnections(Long
 * companyId) { log.info("Getting all connections for company ID: {}",
 * companyId);
 * 
 * return linkRepository.findByCompanyId(companyId) .stream() .map(link -> {
 * Company company =
 * companyRepository.findById(link.getCompanyId()).orElse(null); Supplier
 * supplier = supplierRepository.findById(link.getSupplierId()).orElse(null);
 * return mapToLinkResponse(link, company != null ? company.getName() : null,
 * supplier != null ? supplier.getCompanyName() : null); })
 * .collect(Collectors.toList()); }
 * 
 * public List<CompanySupplierLinkResponse> getSupplierConnections(Long
 * supplierId) { log.info("Getting all connections for supplier ID: {}",
 * supplierId);
 * 
 * return linkRepository.findBySupplierId(supplierId) .stream() .map(link -> {
 * Company company =
 * companyRepository.findById(link.getCompanyId()).orElse(null); Supplier
 * supplier = supplierRepository.findById(link.getSupplierId()).orElse(null);
 * return mapToLinkResponse(link, company != null ? company.getName() : null,
 * supplier != null ? supplier.getCompanyName() : null); })
 * .collect(Collectors.toList()); }
 * 
 * // Incoming Purchase Requests for Supplier public
 * List<PurchaseRequestResponse> getIncomingPurchaseRequests(Long supplierId) {
 * log.info("Getting incoming purchase requests for supplier ID: {}",
 * supplierId);
 * 
 * // Verify supplier exists supplierRepository.findById(supplierId)
 * .orElseThrow(() -> new
 * ResourceNotFoundException("Supplier not found with ID: " + supplierId));
 * 
 * List<PurchaseRequest> purchaseRequests =
 * purchaseRequestRepository.findBySupplierId(supplierId);
 * 
 * return purchaseRequests.stream() .map(this::mapToPurchaseRequestResponse)
 * .collect(Collectors.toList()); }
 * 
 * public List<SupplierProductResponse> getSupplierCatalog(Long supplierId) {
 * return getSupplierProducts(supplierId); }
 * 
 * public List<Supplier> getAllSuppliers() { log.info("Getting all suppliers");
 * return supplierRepository.findAll(); }
 * 
 * // Private mapping methods private SupplierResponse
 * mapToSupplierResponse(Supplier supplier) { return SupplierResponse.builder()
 * .id(supplier.getId()) .userId(supplier.getUserId())
 * .companyName(supplier.getCompanyName()) .email(supplier.getEmail())
 * .phone(supplier.getPhone()) .address(supplier.getAddress())
 * .gstNumber(supplier.getGstNumber()) .website(supplier.getWebsite())
 * .contactPerson(supplier.getContactPerson())
 * .contactPersonPhone(supplier.getContactPersonPhone())
 * .isVerified(supplier.getIsVerified()) .createdAt(supplier.getCreatedAt())
 * .updatedAt(supplier.getUpdatedAt()) .build(); }
 * 
 * private SupplierProductResponse mapToProductResponse(SupplierProduct product)
 * { return SupplierProductResponse.builder() .id(product.getId())
 * .name(product.getName()) .description(product.getDescription())
 * .unit(product.getUnit()) .unitPrice(product.getUnitPrice())
 * .availableQty(product.getAvailableQty()) .category(product.getCategory())
 * .createdAt(product.getCreatedAt()) .updatedAt(product.getUpdatedAt())
 * .build(); }
 * 
 * private CompanySupplierLinkResponse mapToLinkResponse(CompanySupplierLink
 * link, String companyName, String supplierName) { return
 * CompanySupplierLinkResponse.builder() .id(link.getId())
 * .companyId(link.getCompanyId()) .companyName(companyName)
 * .supplierId(link.getSupplierId()) .supplierName(supplierName)
 * .status(link.getStatus()) .createdAt(link.getCreatedAt())
 * .updatedAt(link.getUpdatedAt()) .build(); }
 * 
 * private PurchaseRequestResponse mapToPurchaseRequestResponse(PurchaseRequest
 * request) { Company company =
 * companyRepository.findById(request.getCompanyId()).orElse(null);
 * 
 * return PurchaseRequestResponse.builder() .id(request.getId())
 * .companyId(request.getCompanyId()) .companyName(company != null ?
 * company.getName() : null) .supplierId(request.getSupplierId())
 * .status(request.getStatus()) .totalAmount(request.getTotalAmount())
 * .rejectionReason(request.getRejectionReason())
 * .deliveryNotes(request.getDeliveryNotes())
 * .dispatchedAt(request.getDispatchedAt())
 * .deliveredAt(request.getDeliveredAt()) .createdAt(request.getCreatedAt())
 * .updatedAt(request.getUpdatedAt()) .build(); } }
 */








package com.inventory.modules.supplier.services;

import com.inventory.common.enums.LinkStatus;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.company.entities.Company;
import com.inventory.modules.company.repositories.CompanyRepository;
import com.inventory.modules.purchase.dto.PurchaseRequestResponse;
import com.inventory.modules.purchase.entities.PurchaseRequest;
import com.inventory.modules.purchase.repositories.PurchaseRequestRepository;
import com.inventory.modules.supplier.dto.*;
import com.inventory.modules.supplier.entities.CompanySupplierLink;
import com.inventory.modules.supplier.entities.Supplier;
import com.inventory.modules.supplier.entities.SupplierProduct;
import com.inventory.modules.supplier.entities.SupplierStockMovement;
import com.inventory.modules.supplier.repositories.CompanySupplierLinkRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository productRepository;
    private final CompanySupplierLinkRepository linkRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final SupplierStockMovementRepository supplierStockMovementRepository;
    
    // Get supplier by user ID
    public SupplierResponse getSupplierByUserId(Long userId) {
        log.info("Getting supplier profile for user ID: {}", userId);
        Supplier supplier = supplierRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier profile not found for user ID: " + userId));
        return mapToSupplierResponse(supplier);
    }
    
    // Update supplier profile
    @Transactional
    public SupplierResponse updateSupplier(Long userId, SupplierRequest request) {
        log.info("Updating supplier profile for user ID: {}", userId);
        
        Supplier supplier = supplierRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found for user ID: " + userId));
        
        supplier.setCompanyName(request.getSupplierName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        if (request.getGstNumber() != null) supplier.setGstNumber(request.getGstNumber());
        if (request.getWebsite() != null) supplier.setWebsite(request.getWebsite());
        if (request.getContactPerson() != null) supplier.setContactPerson(request.getContactPerson());
        if (request.getContactPersonPhone() != null) supplier.setContactPersonPhone(request.getContactPersonPhone());
        
        supplier = supplierRepository.save(supplier);
        log.info("Supplier profile updated successfully for ID: {}", supplier.getId());
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(request.getSupplierName());
        userRepository.save(user);
        
        return mapToSupplierResponse(supplier);
    }
    
    // ========== PRODUCT MANAGEMENT ==========
    
    @Transactional
    public SupplierProductResponse addProduct(Long supplierId, SupplierProductRequest request) {
        log.info("Adding product for supplier ID: {}", supplierId);
        
        supplierRepository.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
        
        SupplierProduct product = SupplierProduct.builder()
            .supplierId(supplierId)
            .name(request.getName())
            .description(request.getDescription())
            .unit(request.getUnit())
            .unitPrice(request.getUnitPrice())
            .availableQty(request.getAvailableQty() != null ? request.getAvailableQty() : 0)
            .minimumStockLevel(request.getMinimumStockLevel() != null ? request.getMinimumStockLevel() : 5)
            .category(request.getCategory())
            .isActive(true)
            .build();
        
        product = productRepository.save(product);
        log.info("Product added successfully with ID: {}", product.getId());
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public SupplierProductResponse updateProduct(Long supplierId, Long productId, SupplierProductRequest request) {
        log.info("Updating product ID: {} for supplier ID: {}", productId, supplierId);
        
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnit(request.getUnit());
        product.setUnitPrice(request.getUnitPrice());
        product.setAvailableQty(request.getAvailableQty());
        if (request.getMinimumStockLevel() != null) {
            product.setMinimumStockLevel(request.getMinimumStockLevel());
        }
        product.setCategory(request.getCategory());
        
        product = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", product.getId());
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long supplierId, Long productId) {
        log.info("Deleting product ID: {} for supplier ID: {}", productId, supplierId);
        
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product deactivated successfully");
    }
    
	/*
	 * public List<SupplierProductResponse> getSupplierProducts(Long supplierId) {
	 * log.info("Getting all products for supplier ID: {}", supplierId);
	 * 
	 * List<SupplierProduct> products =
	 * productRepository.findBySupplierId(supplierId);
	 * 
	 * // Null check if (products == null || products.isEmpty()) {
	 * log.info("No products found for supplier ID: {}", supplierId); return new
	 * ArrayList<>(); }
	 * 
	 * return products.stream() .filter(product -> product.getIsActive() != null &&
	 * product.getIsActive()) .map(this::mapToProductResponse)
	 * .collect(Collectors.toList()); }
	 */
    public List<SupplierProductResponse> getSupplierProducts(Long supplierId) {

        log.info("Getting all products for supplier ID: {}", supplierId);

        List<SupplierProduct> products =
                productRepository.findBySupplierIdAndIsActiveTrueOrderByCreatedAtDesc(supplierId);

        if (products.isEmpty()) {
            log.info("No products found for supplier ID: {}", supplierId);
            return new ArrayList<>();
        }

        log.info("Found {} products for supplier {}", products.size(), supplierId);

        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
    
    public SupplierProductResponse getProductById(Long supplierId, Long productId) {
        log.info("Getting product ID: {} for supplier ID: {}", productId, supplierId);
        
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        return mapToProductResponse(product);
    }
    
	/*
	 * public List<SupplierProduct> getLowStockProducts(Long supplierId) {
	 * List<SupplierProduct> products =
	 * productRepository.findBySupplierId(supplierId);
	 * 
	 * if (products == null || products.isEmpty()) { return new ArrayList<>(); }
	 * 
	 * return products.stream() .filter(p -> p.getIsActive() != null &&
	 * p.getIsActive()) .filter(p -> p.getAvailableQty() != null &&
	 * p.getMinimumStockLevel() != null && p.getAvailableQty() <
	 * p.getMinimumStockLevel()) .collect(Collectors.toList()); }
	 */
    public List<SupplierProduct> getLowStockProducts(Long supplierId) {

        List<SupplierProduct> products =
                productRepository.findBySupplierIdAndIsActiveTrue(supplierId);

        return products.stream()
                .filter(p -> {
                    int availableQty = p.getAvailableQty() != null ? p.getAvailableQty() : 0;
                    int minimumStock = p.getMinimumStockLevel() != null ? p.getMinimumStockLevel() : 5;
                    return availableQty < minimumStock;
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public SupplierProductResponse updateMinimumStockLevel(Long supplierId, Long productId, Integer minimumStockLevel) {
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        product.setMinimumStockLevel(minimumStockLevel);
        product = productRepository.save(product);
        
        return mapToProductResponse(product);
    }
    
    // ========== STOCK MANAGEMENT ==========
    
    @Transactional
    public SupplierStockMovementResponse adjustStock(Long supplierId, Long userId, SupplierStockAdjustmentRequest request) {
        SupplierProduct product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        int oldStock = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        int newStock = oldStock;
        String movementType = request.getType();
        
        if ("STOCK_IN".equalsIgnoreCase(request.getType())) {
            newStock = oldStock + request.getQuantity();
        } else if ("STOCK_OUT".equalsIgnoreCase(request.getType())) {
            if (oldStock < request.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + oldStock);
            }
            newStock = oldStock - request.getQuantity();
        } else if ("ADJUSTMENT".equalsIgnoreCase(request.getType())) {
            newStock = request.getQuantity();
        }
        
        product.setAvailableQty(newStock);
        productRepository.save(product);
        
        SupplierStockMovement movement = SupplierStockMovement.builder()
            .supplierProductId(product.getId())
            .type(movementType.toUpperCase())
            .quantity(request.getQuantity())
            .notes(request.getNotes())
            .referenceType("MANUAL_ADJUSTMENT")
            .performedBy(userId)
            .build();
        
        movement = supplierStockMovementRepository.save(movement);
        
        return mapToStockMovementResponse(movement, product.getName());
    }
    
    public List<SupplierStockMovementResponse> getProductStockMovements(Long supplierId, Long productId) {
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        List<SupplierStockMovement> movements = supplierStockMovementRepository.findBySupplierProductIdOrderByCreatedAtDesc(productId);
        
        if (movements == null || movements.isEmpty()) {
            return new ArrayList<>();
        }
        
        return movements.stream()
            .map(movement -> mapToStockMovementResponse(movement, product.getName()))
            .collect(Collectors.toList());
    }
    
    public List<SupplierStockMovementResponse> getAllStockMovements(Long supplierId) {
        List<SupplierStockMovement> movements = supplierStockMovementRepository.findBySupplierId(supplierId);
        
        if (movements == null || movements.isEmpty()) {
            return new ArrayList<>();
        }
        
        return movements.stream()
            .map(movement -> {
                SupplierProduct product = productRepository.findById(movement.getSupplierProductId()).orElse(null);
                String productName = product != null ? product.getName() : "Unknown";
                return mapToStockMovementResponse(movement, productName);
            })
            .collect(Collectors.toList());
    }
    
	/*
	 * public List<SupplierStockReportDTO> getSupplierStockReport(Long supplierId) {
	 * List<SupplierProduct> products =
	 * productRepository.findBySupplierId(supplierId);
	 * 
	 * if (products == null || products.isEmpty()) { return new ArrayList<>(); }
	 * 
	 * return products.stream() .filter(p -> p.getIsActive() != null &&
	 * p.getIsActive()) .map(product -> { int currentStock =
	 * product.getAvailableQty() != null ? product.getAvailableQty() : 0; int
	 * minStock = product.getMinimumStockLevel() != null ?
	 * product.getMinimumStockLevel() : 5; boolean isLowStock = currentStock <
	 * minStock; String status = getStockStatus(currentStock, minStock);
	 * 
	 * return SupplierStockReportDTO.builder() .productId(product.getId())
	 * .productName(product.getName()) .category(product.getCategory())
	 * .currentStock(currentStock) .minimumStockLevel(minStock)
	 * .isLowStock(isLowStock) .unitPrice(product.getUnitPrice() != null ?
	 * product.getUnitPrice() : BigDecimal.ZERO) .totalValue(product.getUnitPrice()
	 * != null ? product.getUnitPrice().multiply(BigDecimal.valueOf(currentStock)) :
	 * BigDecimal.ZERO) .status(status) .build(); }) .collect(Collectors.toList());
	 * }
	 */
    public List<SupplierStockReportDTO> getSupplierStockReport(Long supplierId) {

        List<SupplierProduct> products =
                productRepository.findBySupplierIdAndIsActiveTrue(supplierId);

        return products.stream()
                .map(product -> {

                    int currentStock =
                            product.getAvailableQty() != null ? product.getAvailableQty() : 0;

                    int minimumStock =
                            product.getMinimumStockLevel() != null ? product.getMinimumStockLevel() : 5;

                    boolean isLowStock = currentStock < minimumStock;

                    return SupplierStockReportDTO.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .category(product.getCategory())
                            .currentStock(currentStock)
                            .minimumStockLevel(minimumStock)
                            .isLowStock(isLowStock)
                            .unitPrice(product.getUnitPrice())
                            .totalValue(
                                    product.getUnitPrice()
                                            .multiply(BigDecimal.valueOf(currentStock))
                            )
                            .status(getStockStatus(currentStock, minimumStock))
                            .build();
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public void deductStockOnPurchase(Long supplierId, Long productId, Integer quantity, Long purchaseRequestId) {
        SupplierProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Product not found for this supplier");
        }
        
        int currentQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        int newQty = currentQty - quantity;
        if (newQty < 0) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        
        product.setAvailableQty(newQty);
        productRepository.save(product);
        
        SupplierStockMovement movement = SupplierStockMovement.builder()
            .supplierProductId(product.getId())
            .type("STOCK_OUT")
            .quantity(quantity)
            .referenceType("PURCHASE_ORDER")
            .referenceId(purchaseRequestId)
            .notes("Stock deducted due to purchase order approval")
            .build();
        
        supplierStockMovementRepository.save(movement);
    }
    
    // ========== CONNECTION MANAGEMENT ==========
    
    @Transactional
    public CompanySupplierLinkResponse sendConnectionRequest(Long companyId, CompanySupplierLinkRequest request) {
        log.info("Sending connection request from company ID: {} to supplier ID: {}", companyId, request.getSupplierId());
        
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + request.getSupplierId()));
        
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        
        if (linkRepository.findByCompanyIdAndSupplierId(companyId, request.getSupplierId()).isPresent()) {
            throw new DuplicateResourceException("Connection request already sent to this supplier");
        }
        
        CompanySupplierLink link = CompanySupplierLink.builder()
            .companyId(companyId)
            .supplierId(request.getSupplierId())
            .status(LinkStatus.PENDING)
            .build();
        
        link = linkRepository.save(link);
        log.info("Connection request sent successfully with link ID: {}", link.getId());
        
        return mapToLinkResponse(link, company.getName(), supplier.getCompanyName());
    }
    
    @Transactional
    public CompanySupplierLinkResponse updateConnectionStatus(Long supplierId, Long linkId, UpdateLinkStatusRequest request) {
        log.info("Updating connection status for link ID: {} to status: {}", linkId, request.getStatus());
        
        CompanySupplierLink link = linkRepository.findById(linkId)
            .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + linkId));
        
        if (!link.getSupplierId().equals(supplierId)) {
            throw new ResourceNotFoundException("Connection not found for this supplier");
        }
        
        link.setStatus(request.getStatus());
        link = linkRepository.save(link);
        
        Company company = companyRepository.findById(link.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        log.info("Connection status updated successfully");
        
        return mapToLinkResponse(link, company.getName(), supplier.getCompanyName());
    }
    
    public List<CompanySupplierLinkResponse> getCompanyConnections(Long companyId) {
        log.info("Getting all connections for company ID: {}", companyId);
        
        List<CompanySupplierLink> links = linkRepository.findByCompanyId(companyId);
        
        if (links == null || links.isEmpty()) {
            return new ArrayList<>();
        }
        
        return links.stream()
            .map(link -> {
                Company company = companyRepository.findById(link.getCompanyId()).orElse(null);
                Supplier supplier = supplierRepository.findById(link.getSupplierId()).orElse(null);
                return mapToLinkResponse(link, 
                    company != null ? company.getName() : null,
                    supplier != null ? supplier.getCompanyName() : null);
            })
            .collect(Collectors.toList());
    }
    
    public List<CompanySupplierLinkResponse> getSupplierConnections(Long supplierId) {
        log.info("Getting all connections for supplier ID: {}", supplierId);
        
        List<CompanySupplierLink> links = linkRepository.findBySupplierId(supplierId);
        
        if (links == null || links.isEmpty()) {
            return new ArrayList<>();
        }
        
        return links.stream()
            .map(link -> {
                Company company = companyRepository.findById(link.getCompanyId()).orElse(null);
                Supplier supplier = supplierRepository.findById(link.getSupplierId()).orElse(null);
                return mapToLinkResponse(link, 
                    company != null ? company.getName() : null,
                    supplier != null ? supplier.getCompanyName() : null);
            })
            .collect(Collectors.toList());
    }
    
    // ========== PURCHASE REQUESTS ==========
    
    public List<PurchaseRequestResponse> getIncomingPurchaseRequests(Long supplierId) {
        log.info("Getting incoming purchase requests for supplier ID: {}", supplierId);
        
        supplierRepository.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
        
        List<PurchaseRequest> purchaseRequests = purchaseRequestRepository.findBySupplierId(supplierId);
        
        if (purchaseRequests == null || purchaseRequests.isEmpty()) {
            return new ArrayList<>();
        }
        
        return purchaseRequests.stream()
            .map(this::mapToPurchaseRequestResponse)
            .collect(Collectors.toList());
    }
    
    public List<SupplierProductResponse> getSupplierCatalog(Long supplierId) {
        return getSupplierProducts(supplierId);
    }
    
    public List<Supplier> getAllSuppliers() {
        log.info("Getting all suppliers");
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers != null ? suppliers : new ArrayList<>();
    }
    
    // ========== PRIVATE MAPPING METHODS ==========
    
    private String getStockStatus(Integer currentStock, Integer minimumStock) {
        if (currentStock == null || currentStock == 0) return "OUT_OF_STOCK";
        if (currentStock < minimumStock) return "LOW_STOCK";
        if (currentStock <= minimumStock * 2) return "MODERATE";
        return "HEALTHY";
    }
    
    public SupplierProductResponse mapToProductResponse(SupplierProduct product) {
        if (product == null) {
            return null;
        }
        
        int availableQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        int minStock = product.getMinimumStockLevel() != null ? product.getMinimumStockLevel() : 5;
        boolean isLowStock = availableQty < minStock;
        boolean isActive = product.getIsActive() != null && product.getIsActive();
        
        return SupplierProductResponse.builder()
            .id(product.getId())
            .name(product.getName() != null ? product.getName() : "")
            .description(product.getDescription() != null ? product.getDescription() : "")
            .unit(product.getUnit() != null ? product.getUnit() : "Pieces")
            .unitPrice(product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO)
            .availableQty(availableQty)
            .minimumStockLevel(minStock)
            .isLowStock(isLowStock)
            .category(product.getCategory() != null ? product.getCategory() : "")
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
    
    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        
        return SupplierResponse.builder()
            .id(supplier.getId())
            .userId(supplier.getUserId())
            .companyName(supplier.getCompanyName())
            .email(supplier.getEmail())
            .phone(supplier.getPhone())
            .address(supplier.getAddress())
            .gstNumber(supplier.getGstNumber())
            .website(supplier.getWebsite())
            .contactPerson(supplier.getContactPerson())
            .contactPersonPhone(supplier.getContactPersonPhone())
            .isVerified(supplier.getIsVerified() != null && supplier.getIsVerified())
            .createdAt(supplier.getCreatedAt())
            .updatedAt(supplier.getUpdatedAt())
            .build();
    }
    
    private CompanySupplierLinkResponse mapToLinkResponse(CompanySupplierLink link, String companyName, String supplierName) {
        if (link == null) {
            return null;
        }
        
        return CompanySupplierLinkResponse.builder()
            .id(link.getId())
            .companyId(link.getCompanyId())
            .companyName(companyName)
            .supplierId(link.getSupplierId())
            .supplierName(supplierName)
            .status(link.getStatus())
            .createdAt(link.getCreatedAt())
            .updatedAt(link.getUpdatedAt())
            .build();
    }
    
    private PurchaseRequestResponse mapToPurchaseRequestResponse(PurchaseRequest request) {
        if (request == null) {
            return null;
        }
        
        Company company = companyRepository.findById(request.getCompanyId()).orElse(null);
        
        return PurchaseRequestResponse.builder()
            .id(request.getId())
            .companyId(request.getCompanyId())
            .companyName(company != null ? company.getName() : null)
            .supplierId(request.getSupplierId())
            .status(request.getStatus())
            .totalAmount(request.getTotalAmount())
            .rejectionReason(request.getRejectionReason())
            .deliveryNotes(request.getDeliveryNotes())
            .dispatchedAt(request.getDispatchedAt())
            .deliveredAt(request.getDeliveredAt())
            .createdAt(request.getCreatedAt())
            .updatedAt(request.getUpdatedAt())
            .build();
    }
    
    private SupplierStockMovementResponse mapToStockMovementResponse(SupplierStockMovement movement, String productName) {
        if (movement == null) {
            return null;
        }
        
        return SupplierStockMovementResponse.builder()
            .id(movement.getId())
            .productId(movement.getSupplierProductId())
            .productName(productName)
            .type(movement.getType())
            .quantity(movement.getQuantity())
            .referenceType(movement.getReferenceType())
            .notes(movement.getNotes())
            .createdAt(movement.getCreatedAt())
            .build();
    }
    
    public List<SupplierCustomerResponse> getCustomers(Long supplierId) {

        List<CompanySupplierLink> links =
                linkRepository.findBySupplierId(supplierId);

        return links.stream()
                .filter(link -> link.getStatus() == LinkStatus.ACCEPTED)
                .map(link -> {

                    Company company = companyRepository
                            .findById(link.getCompanyId())
                            .orElse(null);

                    if (company == null) {
                        return null;
                    }

                    return SupplierCustomerResponse.builder()
                            .companyId(company.getId())
                            .companyName(company.getName())
                            .email(company.getEmail())
                            .phone(company.getPhone())
                            .connectedAt(link.getCreatedAt())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
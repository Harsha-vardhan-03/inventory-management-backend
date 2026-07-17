package com.inventory.modules.reports.services;

import com.inventory.common.enums.OrderStatus;
import com.inventory.modules.inventory.entities.Product;
import com.inventory.modules.inventory.repositories.ProductRepository;
import com.inventory.modules.inventory.repositories.StockMovementRepository;
import com.inventory.modules.purchase.repositories.PurchaseRequestRepository;
import com.inventory.modules.sales.repositories.SalesOrderRepository;
import com.inventory.modules.sales.repositories.SalesOrderItemRepository;
import com.inventory.modules.supplier.repositories.SupplierRepository;
import com.inventory.modules.sales.repositories.CustomerRepository;
import com.inventory.modules.reports.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsService {
    
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    
    public List<InventorySummaryDTO> getInventorySummary(Long companyId) {
        log.info("Generating inventory summary for company: {}", companyId);
        List<Product> products = productRepository.findByCompanyId(companyId);
        
        return products.stream()
            .map(product -> InventorySummaryDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .category(product.getCategory())
                .currentStock(product.getCurrentStock())
                .minimumStockLevel(product.getMinimumStockLevel())
                .isLowStock(product.getCurrentStock() < product.getMinimumStockLevel())
                .unitPrice(product.getUnitPrice())
                .totalValue(product.getUnitPrice().multiply(BigDecimal.valueOf(product.getCurrentStock())))
                .status(getStockStatus(product.getCurrentStock(), product.getMinimumStockLevel()))
                .build())
            .collect(Collectors.toList());
    }
    
    public List<StockMovementSummaryDTO> getStockMovements(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting stock movements for company: {} from {} to {}", companyId, startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return stockMovementRepository.findByDateRange(companyId, startDateTime, endDateTime)
            .stream()
            .map(movement -> {
                Product product = productRepository.findById(movement.getProductId()).orElse(null);
                return StockMovementSummaryDTO.builder()
                    .movementId(movement.getId())
                    .productName(product != null ? product.getName() : null)
                    .sku(product != null ? product.getSku() : null)
                    .type(movement.getType())
                    .quantity(movement.getQuantity())
                    .referenceType(movement.getReferenceType())
                    .notes(movement.getNotes())
                    .performedBy(String.valueOf(movement.getPerformedBy()))
                    .createdAt(movement.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public List<SalesSummaryDTO> getSalesSummary(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating sales summary for company: {} from {} to {}", companyId, startDate, endDate);
        
        return startDate.datesUntil(endDate.plusDays(1))
            .map(date -> {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);
                
                BigDecimal revenue = salesOrderRepository.getTotalRevenue(companyId, start, end);
                Long orderCount = salesOrderRepository.getOrderCount(companyId, start, end);
                Integer totalItemsSold = salesOrderItemRepository.getTotalItemsSold(companyId, start, end);
                BigDecimal totalDiscount = salesOrderRepository.getTotalDiscount(companyId, start, end);
                
                Integer itemsSold = totalItemsSold != null ? totalItemsSold : 0;
                BigDecimal discount = totalDiscount != null ? totalDiscount : BigDecimal.ZERO;
                Long orders = orderCount != null ? orderCount : 0L;
                BigDecimal rev = revenue != null ? revenue : BigDecimal.ZERO;
                
                return SalesSummaryDTO.builder()
                    .date(date)
                    .totalOrders(orders)
                    .totalRevenue(rev)
                    .averageOrderValue(orders > 0 ? rev.divide(BigDecimal.valueOf(orders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .totalDiscount(discount)
                    .totalItemsSold(itemsSold)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public List<PurchaseSummaryDTO> getPurchaseSummary(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating purchase summary for company: {} from {} to {}", companyId, startDate, endDate);
        
        return startDate.datesUntil(endDate.plusDays(1))
            .map(date -> {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);
                
                List<com.inventory.modules.purchase.entities.PurchaseRequest> purchases = 
                    purchaseRequestRepository.findByCompanyId(companyId);
                
                List<com.inventory.modules.purchase.entities.PurchaseRequest> dailyPurchases = purchases.stream()
                    .filter(p -> p.getCreatedAt().isAfter(start) && p.getCreatedAt().isBefore(end))
                    .collect(Collectors.toList());
                
                long approved = dailyPurchases.stream().filter(p -> p.getStatus() == OrderStatus.APPROVED).count();
                long pending = dailyPurchases.stream().filter(p -> p.getStatus() == OrderStatus.PENDING).count();
                long rejected = dailyPurchases.stream().filter(p -> p.getStatus() == OrderStatus.REJECTED).count();
                long delivered = dailyPurchases.stream().filter(p -> p.getStatus() == OrderStatus.DELIVERED).count();
                
                BigDecimal totalAmount = dailyPurchases.stream()
                    .map(com.inventory.modules.purchase.entities.PurchaseRequest::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Integer totalItemsPurchased = purchaseRequestRepository.getTotalItemsPurchased(companyId, start, end);
                Integer itemsPurchased = totalItemsPurchased != null ? totalItemsPurchased : 0;
                
                return PurchaseSummaryDTO.builder()
                    .date(date)
                    .totalPurchaseOrders((long) dailyPurchases.size())
                    .totalPurchaseAmount(totalAmount)
                    .totalItemsPurchased(itemsPurchased)
                    .approvedOrders(approved)
                    .pendingOrders(pending)
                    .rejectedOrders(rejected)
                    .deliveredOrders(delivered)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public List<LowStockAlertDTO> getLowStockAlerts(Long companyId) {
        log.info("Getting low stock alerts for company: {}", companyId);
        List<Product> lowStockProducts = productRepository.findLowStockProducts(companyId);
        
        return lowStockProducts.stream()
            .map(product -> {
                String urgencyLevel = getUrgencyLevel(product.getCurrentStock(), product.getMinimumStockLevel());
                int reorderQty = calculateReorderQuantity(product.getMinimumStockLevel(), product.getCurrentStock());
                
                return LowStockAlertDTO.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .sku(product.getSku())
                    .category(product.getCategory())
                    .currentStock(product.getCurrentStock())
                    .minimumStockLevel(product.getMinimumStockLevel())
                    .reorderQuantity(reorderQty)
                    .urgencyLevel(urgencyLevel)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public List<TopProductDTO> getTopProducts(Long companyId, int limit) {
        log.info("Getting top {} selling products for company: {}", limit, companyId);
        List<Object[]> results = salesOrderItemRepository.findTopSellingProducts(companyId);
        
        return results.stream()
            .limit(limit)
            .map(result -> {
                Long productId = (Long) result[0];
                Long totalQuantity = (Long) result[1];
                Product product = productRepository.findById(productId).orElse(null);
                
                BigDecimal totalRevenue = BigDecimal.ZERO;
                if (product != null && product.getSellingPrice() != null) {
                    totalRevenue = product.getSellingPrice().multiply(BigDecimal.valueOf(totalQuantity));
                }
                
                return TopProductDTO.builder()
                    .productId(productId)
                    .productName(product != null ? product.getName() : "Unknown")
                    .sku(product != null ? product.getSku() : "N/A")
                    .category(product != null ? product.getCategory() : "Uncategorized")
                    .totalQuantitySold(totalQuantity != null ? totalQuantity : 0L)
                    .totalRevenue(totalRevenue)
                    .currentStock(product != null ? product.getCurrentStock() : 0)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public DashboardSummaryDTO getDashboardSummary(Long companyId) {
        log.info("Generating dashboard summary for company: {}", companyId);
        
        List<Product> products = productRepository.findByCompanyId(companyId);
        long lowStock = products.stream().filter(p -> p.getCurrentStock() < p.getMinimumStockLevel()).count();
        long outOfStock = products.stream().filter(p -> p.getCurrentStock() == 0).count();
        long pendingPurchaseOrders = purchaseRequestRepository.findByCompanyIdAndStatus(companyId, OrderStatus.PENDING).size();
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        
        BigDecimal todayRevenue = salesOrderRepository.getTotalRevenue(companyId, todayStart, todayEnd);
        Long todayOrders = salesOrderRepository.getOrderCount(companyId, todayStart, todayEnd);
        Integer todayItemsSold = salesOrderItemRepository.getTotalItemsSold(companyId, todayStart, todayEnd);
        
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDateTime monthStartDateTime = monthStart.atStartOfDay();
        BigDecimal monthRevenue = salesOrderRepository.getTotalRevenue(companyId, monthStartDateTime, todayEnd);
        Long monthOrders = salesOrderRepository.getOrderCount(companyId, monthStartDateTime, todayEnd);
        Integer monthItemsSold = salesOrderItemRepository.getTotalItemsSold(companyId, monthStartDateTime, todayEnd);
        
        long totalCustomers = customerRepository.findByCompanyId(companyId).size();
        long totalSuppliers = supplierRepository.count();
        
        BigDecimal totalInventoryValue = products.stream()
            .map(p -> p.getUnitPrice().multiply(BigDecimal.valueOf(p.getCurrentStock())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return DashboardSummaryDTO.builder()
            .totalProducts((long) products.size())
            .lowStockCount(lowStock)
            .outOfStockCount(outOfStock)
            .pendingPurchaseOrders(pendingPurchaseOrders)
            .todaySalesCount(todayOrders != null ? todayOrders : 0L)
            .todaySalesRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
            .todayItemsSold(todayItemsSold != null ? todayItemsSold : 0)
            .currentMonthSalesCount(monthOrders != null ? monthOrders : 0L)
            .currentMonthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO)
            .currentMonthItemsSold(monthItemsSold != null ? monthItemsSold : 0)
            .totalCustomers(totalCustomers)
            .totalSuppliers(totalSuppliers)
            .totalInventoryValue(totalInventoryValue)
            .build();
    }
    
    private String getStockStatus(Integer currentStock, Integer minimumStock) {
        if (currentStock == 0) return "OUT_OF_STOCK";
        if (currentStock < minimumStock) return "LOW_STOCK";
        if (currentStock <= minimumStock * 2) return "MODERATE";
        return "HEALTHY";
    }
    
    private String getUrgencyLevel(Integer currentStock, Integer minimumStock) {
        if (currentStock == 0) return "CRITICAL";
        if (currentStock < minimumStock / 2) return "HIGH";
        if (currentStock < minimumStock) return "MEDIUM";
        return "LOW";
    }
    
    private int calculateReorderQuantity(Integer minimumStock, Integer currentStock) {
        if (currentStock >= minimumStock) return 0;
        return (minimumStock * 2) - currentStock;
    }
}
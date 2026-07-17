package com.inventory.modules.sales.services;

import com.inventory.common.enums.StockMovementType;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.inventory.entities.Product;
import com.inventory.modules.inventory.entities.StockMovement;
import com.inventory.modules.inventory.repositories.ProductRepository;
import com.inventory.modules.inventory.repositories.StockMovementRepository;
import com.inventory.modules.sales.dto.*;
import com.inventory.modules.sales.entities.Customer;
import com.inventory.modules.sales.entities.SalesOrder;
import com.inventory.modules.sales.entities.SalesOrderItem;
import com.inventory.modules.sales.repositories.CustomerRepository;
import com.inventory.modules.sales.repositories.SalesOrderItemRepository;
import com.inventory.modules.sales.repositories.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {
    
    private final CustomerRepository customerRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    
    @Transactional
    public CustomerResponse addCustomer(Long companyId, CustomerRequest request) {
        Customer customer = Customer.builder()
            .companyId(companyId)
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .build();
        
        customer = customerRepository.save(customer);
        return mapToCustomerResponse(customer);
    }
    
    public List<CustomerResponse> getCompanyCustomers(Long companyId) {
        return customerRepository.findByCompanyId(companyId)
            .stream()
            .map(this::mapToCustomerResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public SalesOrderResponse createSalesOrder(Long companyId, Long userId, SalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!customer.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Customer not found for this company");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Validate stock availability
        for (SaleItemDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            if (product.getCurrentStock() < item.getQuantity()) {
                throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName() + 
                    ". Available: " + product.getCurrentStock() + ", Requested: " + item.getQuantity());
            }
            
            totalAmount = totalAmount.add(BigDecimal.valueOf(item.getQuantity())
                .multiply(BigDecimal.valueOf(item.getUnitPrice())));
        }
        
        BigDecimal finalAmount = totalAmount.subtract(request.getDiscount());
        
        // Create sales order
        SalesOrder order = SalesOrder.builder()
            .companyId(companyId)
            .customerId(request.getCustomerId())
            .totalAmount(totalAmount)
            .discount(request.getDiscount())
            .finalAmount(finalAmount)
            .paymentMode(request.getPaymentMode())
            .status("COMPLETED")
            .build();
        
        order = salesOrderRepository.save(order);
        
        // Create order items and update stock
        for (SaleItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            SalesOrderItem item = SalesOrderItem.builder()
                .salesOrderId(order.getId())
                .productId(itemDTO.getProductId())
                .productName(itemDTO.getProductName())
                .quantity(itemDTO.getQuantity())
                .unitPrice(BigDecimal.valueOf(itemDTO.getUnitPrice()))
                .totalPrice(BigDecimal.valueOf(itemDTO.getQuantity()).multiply(BigDecimal.valueOf(itemDTO.getUnitPrice())))
                .build();
            
            orderItemRepository.save(item);
            
            // Update stock
            int oldStock = product.getCurrentStock();
            product.setCurrentStock(oldStock - itemDTO.getQuantity());
            productRepository.save(product);
            
            // Create stock movement
            StockMovement movement = StockMovement.builder()
                .productId(product.getId())
                .companyId(companyId)
                .type(StockMovementType.STOCK_OUT)
                .quantity(itemDTO.getQuantity())
                .referenceId(order.getId())
                .referenceType("SALES_ORDER")
                .notes("Sale to customer: " + customer.getName())
                .performedBy(userId)
                .build();
            
            stockMovementRepository.save(movement);
        }
        
        return mapToOrderResponse(order);
    }
    
    public List<SalesOrderResponse> getCompanyOrders(Long companyId) {
        return salesOrderRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
            .stream()
            .map(this::mapToOrderResponse)
            .collect(Collectors.toList());
    }
    
    public SalesOrderResponse getOrderById(Long companyId, Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Order not found for this company");
        }
        
        return mapToOrderResponse(order);
    }
    
    public List<SaleItemDTO> getOrderItems(Long orderId) {
        List<SalesOrderItem> items = orderItemRepository.findBySalesOrderId(orderId);
        return items.stream()
            .map(item -> {
                SaleItemDTO dto = new SaleItemDTO();
                dto.setProductId(item.getProductId());
                dto.setProductName(item.getProductName());
                dto.setQuantity(item.getQuantity());
                dto.setUnitPrice(item.getUnitPrice().doubleValue());
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
            .id(customer.getId())
            .name(customer.getName())
            .email(customer.getEmail())
            .phone(customer.getPhone())
            .address(customer.getAddress())
            .createdAt(customer.getCreatedAt())
            .build();
    }
    
    private SalesOrderResponse mapToOrderResponse(SalesOrder order) {
        Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        List<SaleItemDTO> items = getOrderItems(order.getId());
        
        return SalesOrderResponse.builder()
            .id(order.getId())
            .customerId(order.getCustomerId())
            .customerName(customer != null ? customer.getName() : null)
            .totalAmount(order.getTotalAmount())
            .discount(order.getDiscount())
            .finalAmount(order.getFinalAmount())
            .paymentMode(order.getPaymentMode())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .items(items)
            .build();
    }
}
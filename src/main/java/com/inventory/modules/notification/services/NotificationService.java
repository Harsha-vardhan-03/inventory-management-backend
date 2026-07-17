package com.inventory.modules.notification.services;

import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.modules.notification.dto.MarkReadRequest;
import com.inventory.modules.notification.dto.NotificationDTO;
import com.inventory.modules.notification.dto.NotificationRequest;
import com.inventory.modules.notification.entities.Notification;
import com.inventory.modules.notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Transactional
    public NotificationDTO createNotification(Long userId, Long companyId, NotificationRequest request) {
        log.info("Creating notification for user: {}", userId);
        
        Notification notification = Notification.builder()
            .userId(userId)
            .companyId(companyId)
            .title(request.getTitle())
            .message(request.getMessage())
            .type(Notification.NotificationType.valueOf(request.getType()))
            .isRead(false)
            .link(request.getLink())
            .icon(request.getIcon())
            .build();
        
        notification = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", notification.getId());
        
        return mapToDTO(notification);
    }
    
    @Transactional
    public void createBulkNotifications(List<Long> userIds, Long companyId, NotificationRequest request) {
        log.info("Creating bulk notifications for {} users", userIds.size());
        
        List<Notification> notifications = userIds.stream()
            .map(userId -> Notification.builder()
                .userId(userId)
                .companyId(companyId)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(Notification.NotificationType.valueOf(request.getType()))
                .isRead(false)
                .link(request.getLink())
                .icon(request.getIcon())
                .build())
            .collect(Collectors.toList());
        
        notificationRepository.saveAll(notifications);
        log.info("Bulk notifications created successfully");
    }
    
    public List<NotificationDTO> getUserNotifications(Long userId) {
        log.info("Getting notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public Page<NotificationDTO> getUserNotificationsPaginated(Long userId, Pageable pageable) {
        log.info("Getting paginated notifications for user: {}", userId);
        
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::mapToDTO);
    }
    
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        log.info("Getting unread notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public Long countUnreadNotifications(Long userId) {
        log.info("Counting unread notifications for user: {}", userId);
        return notificationRepository.countUnreadNotifications(userId);
    }
    
    @Transactional
    public int markAsRead(Long userId, MarkReadRequest request) {
        log.info("Marking notifications as read for user: {}", userId);
        
        int updatedCount = 0;
        
        try {
            if (request.getMarkAll() != null && request.getMarkAll()) {
                log.info("Marking all notifications as read for user: {}", userId);
                updatedCount = notificationRepository.markAllAsRead(userId);
            } else if (request.getNotificationIds() != null && !request.getNotificationIds().isEmpty()) {
                log.info("Marking {} notifications as read for user: {}", request.getNotificationIds().size(), userId);
                updatedCount = notificationRepository.markAsRead(userId, request.getNotificationIds());
                
                // If JPQL doesn't work, try native query
                if (updatedCount == 0) {
                    log.warn("JPQL update returned 0, trying native query");
                    updatedCount = notificationRepository.markAsReadNative(userId, request.getNotificationIds());
                }
            } else {
                log.warn("No notifications to mark as read. MarkAll: {}, NotificationIds: {}", 
                    request.getMarkAll(), request.getNotificationIds());
            }
        } catch (Exception e) {
            log.error("Error marking notifications as read: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
        
        log.info("Successfully marked {} notifications as read", updatedCount);
        return updatedCount;
    }
    
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("Deleting notification: {} for user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("You don't have permission to delete this notification");
        }
        
        notificationRepository.delete(notification);
        log.info("Notification deleted successfully");
    }
    
    // ========== PREDEFINED NOTIFICATION TYPES ==========
    
    @Transactional
    public void notifyPurchaseRequestCreated(Long companyId, Long userId, String supplierName, Long purchaseRequestId) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Purchase Request Created");
        request.setMessage("Your purchase request to " + supplierName + " has been created successfully.");
        request.setType("PURCHASE");
        request.setLink("/purchase-requests/" + purchaseRequestId);
        request.setIcon("shopping_cart");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifySupplierApproved(Long companyId, Long userId, String supplierName, Long purchaseRequestId) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Purchase Request Approved");
        request.setMessage(supplierName + " has approved your purchase request.");
        request.setType("SUCCESS");
        request.setLink("/purchase-requests/" + purchaseRequestId);
        request.setIcon("check_circle");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifySupplierRejected(Long companyId, Long userId, String supplierName, Long purchaseRequestId, String reason) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Purchase Request Rejected");
        request.setMessage(supplierName + " has rejected your purchase request. Reason: " + reason);
        request.setType("ERROR");
        request.setLink("/purchase-requests/" + purchaseRequestId);
        request.setIcon("cancel");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifySupplierDispatched(Long companyId, Long userId, String supplierName, Long purchaseRequestId) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Purchase Request Dispatched");
        request.setMessage(supplierName + " has dispatched your purchase request.");
        request.setType("INFO");
        request.setLink("/purchase-requests/" + purchaseRequestId);
        request.setIcon("local_shipping");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifyPurchaseDelivered(Long companyId, Long userId, Long purchaseRequestId) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Purchase Request Delivered");
        request.setMessage("Your purchase request has been delivered successfully. Stock has been updated.");
        request.setType("SUCCESS");
        request.setLink("/purchase-requests/" + purchaseRequestId);
        request.setIcon("check_circle");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifyLowStockAlert(Long companyId, Long userId, String productName, Integer currentStock) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Low Stock Alert");
        request.setMessage("Product '" + productName + "' is running low. Current stock: " + currentStock);
        request.setType("WARNING");
        request.setLink("/inventory/products");
        request.setIcon("warning");
        createNotification(userId, companyId, request);
    }
    
    @Transactional
    public void notifyNewSale(Long companyId, Long userId, String customerName, Double amount) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle("New Sale Created");
        request.setMessage("Sale to " + customerName + " created. Amount: $" + amount);
        request.setType("SALES");
        request.setLink("/sales");
        request.setIcon("attach_money");
        createNotification(userId, companyId, request);
    }
    
    // ========== MAPPING METHODS ==========
    
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .type(notification.getType().name())
            .isRead(notification.getIsRead())
            .link(notification.getLink())
            .icon(notification.getIcon())
            .createdAt(notification.getCreatedAt())
            .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
            .build();
    }
    
    private String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        
        if (seconds < 0) {
            return "Just now";
        }
        
        if (seconds < 60) {
            return seconds + "s ago";
        } else if (minutes < 60) {
            return minutes + "m ago";
        } else if (hours < 24) {
            return hours + "h ago";
        } else if (days < 7) {
            return days + "d ago";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + "w ago";
        } else if (days < 365) {
            long months = days / 30;
            return months + "mo ago";
        } else {
            long years = days / 365;
            return years + "y ago";
        }
    }
}
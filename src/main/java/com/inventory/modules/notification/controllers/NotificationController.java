package com.inventory.modules.notification.controllers;

import com.inventory.common.response.ApiResponse;
import com.inventory.modules.notification.dto.MarkReadRequest;
import com.inventory.modules.notification.dto.NotificationDTO;
import com.inventory.modules.notification.services.NotificationService;
import com.inventory.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUserNotifications(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting notifications for user: {}", currentUser.getId());
        List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @GetMapping("/paginated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getUserNotificationsPaginated(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            Pageable pageable) {
        log.info("Getting paginated notifications for user: {}", currentUser.getId());
        Page<NotificationDTO> notifications = notificationService.getUserNotificationsPaginated(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Getting unread notifications for user: {}", currentUser.getId());
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @GetMapping("/count/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Counting unread notifications for user: {}", currentUser.getId());
        Long count = notificationService.countUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @PatchMapping("/mark-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Integer>> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody MarkReadRequest request) {
        log.info("Marking notifications as read for user: {}", currentUser.getId());
        
        // Log the request for debugging
        log.debug("MarkReadRequest: markAll={}, notificationIds={}", 
            request.getMarkAll(), request.getNotificationIds());
        
        int count = notificationService.markAsRead(currentUser.getId(), request);
        String message = count + " notification" + (count != 1 ? "s" : "") + " marked as read";
        return ResponseEntity.ok(ApiResponse.success(count, message));
    }
    
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long notificationId) {
        log.info("Deleting notification: {} for user: {}", notificationId, currentUser.getId());
        notificationService.deleteNotification(currentUser.getId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted successfully"));
    }
    
    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> testNotification() {
        log.info("Test notification endpoint called");
        return ResponseEntity.ok(ApiResponse.success("Notification service is working!"));
    }
}
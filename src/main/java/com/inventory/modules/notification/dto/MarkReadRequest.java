package com.inventory.modules.notification.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarkReadRequest {
    private List<Long> notificationIds;
    private Boolean markAll = false;
}
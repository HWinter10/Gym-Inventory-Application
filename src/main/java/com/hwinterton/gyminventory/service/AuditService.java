package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.AuditLogRepository;

public class AuditService {
    private final AuditLogRepository repo = new AuditLogRepository();

    public void log(Long userId, String action, String details) {
        repo.insert(userId, action, details);
    }
}
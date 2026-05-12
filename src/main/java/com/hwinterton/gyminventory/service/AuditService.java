package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.AuditLogRepository;
import com.hwinterton.gyminventory.domain.AuditLog;

import java.util.List;

/**
 * Provides audit logging behavior for the application. 
 * 
 * <p>This service records security and system events, and loads recent audit records
 * for display in the JavaFX action log viewer.</p>
 * 
 * <p>Uses {@link AuditLogRepository} for audit log database access.</p>
 */
public class AuditService {
    private final AuditLogRepository repo = new AuditLogRepository(); // audit log persistence access

    /**
     * Records a security or system event
     * 
     * @param userId user id connected to action, null if unavailable
     * @param action action being recorded
     * @param details extra information about the action
     */
    public void log(Long userId, String action, String details) {
    	
    	// insert audit record with user id, action, and details
        repo.insert(userId, action, details);
    }
    
    /**
     * Loads recent audit log records
     * 
     * @param limit the maximum number of rows to load
     * @return recent audit log recorder ordered newest first
     */
    public List<AuditLog> findRecent(int limit) {

        // delegate audit log read logic to repository
        return repo.findRecent(limit);
    }
}
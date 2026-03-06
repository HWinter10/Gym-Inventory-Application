/*
 * Purpose:
 * - writes security relevant events to audit log
 * 
 * Function:
 * - accepts user id, action, details
 * - calls AuditLogRepository to insert row
 * - used by login, user management, password change flows
 * 
 * Dependencies:
 * - AuditLogRepository for persistence
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.AuditLogRepository;

public class AuditService {
    private final AuditLogRepository repo = new AuditLogRepository(); // audit log persistence access

    // Method - record security or system event
    public void log(Long userId, String action, String details) {
    	
    	// insert audit record with user id, action, and details
        repo.insert(userId, action, details);
    }
}
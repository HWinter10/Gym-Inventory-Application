package com.hwinterton.gyminventory.domain;


/**
 * Represents one row from the audit_log table.
 * 
 * <p>An audit log record stores a security or system action that happened in
 * the application, such as a login attempt, user management change, sale, or
 * inventory adjustment.</p>
 */

public class AuditLog {
	
	private final long id;
	private final String createdAt;
	private final Long actorUserId;
	private final String username;
	private final String action;
	private final String details;

	public AuditLog(long id, String createdAt, Long actorUserId, String username, String action, String details) {
		this.id = id;
		this.createdAt = createdAt;
		this.actorUserId = actorUserId;
		this.username = username;
		this.action = action;
		this.details = details;
	}

	// getters
	public long getId() {
		return id;
	}
	public String getCreatedAt()  {
		return createdAt;
	}
	public Long getActorUserId() {
		return actorUserId;
	}
	public String getUsername() {
		return username;
	}
	public String getAction() {
		return action;
	}
	public String getDetails() {
		return details;
	}
}













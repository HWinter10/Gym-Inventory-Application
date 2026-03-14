/*
 * Purpose:
 * - represents recorded inventory adjustment event
 * 
 * Function:
 * - stores product reference, quantity change, reason code, notes, user id, and timestamp
 * - supports traceability and future reporting
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public class InventoryAdjustment {
    private final long id; // unique adjustment id
    private final long productId; // adjusted product id
    private final int quantityChange; // signed quantity change
    private final String reasonCode; // required adjustment reason code
    private final String notes; // optional explanation notes
    private final long adjustedByUserId; // user who recorded adjustment
    private final String createdAt; // timestamp recorded by database

    // Method - construct inventory adjustment domain object
    public InventoryAdjustment(long id, long productId, int quantityChange, String reasonCode, String notes, long adjustedByUserId, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantityChange = quantityChange;
        this.reasonCode = reasonCode;
        this.notes = notes;
        this.adjustedByUserId = adjustedByUserId;
        this.createdAt = createdAt;
    }

    // Method - return adjustment id
    public long getId() {
        return id;
    }

    // Method - return product id
    public long getProductId() {
        return productId;
    }

    // Method - return signed quantity change
    public int getQuantityChange() {
        return quantityChange;
    }

    // Method - return reason code
    public String getReasonCode() {
        return reasonCode;
    }

    // Method - return notes
    public String getNotes() {
        return notes;
    }

    // Method - return user id who recorded adjustment
    public long getAdjustedByUserId() {
        return adjustedByUserId;
    }

    // Method - return timestamp
    public String getCreatedAt() {
        return createdAt;
    }
}
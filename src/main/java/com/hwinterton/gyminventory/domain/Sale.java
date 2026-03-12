/*
 * Purpose:
 * - represents recorded product sale
 * 
 * Function:
 * - stores sale identity, product reference, quantity sold, actor user id, and timestamp
 * - supports future reporting and traceability
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public class Sale {
    private final long id;
    private final long productId;
    private final int quantitySold;
    private final long soldByUserId;
    private final String createdAt;

    // Method - construct sale domain object
    public Sale(long id, long productId, int quantitySold, long soldByUserId, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.soldByUserId = soldByUserId;
        this.createdAt = createdAt;
    }

    // Method - return sale id
    public long getId() {
        return id;
    }

    // Method - return product id
    public long getProductId() {
        return productId;
    }

    // Method - return quantity sold
    public int getQuantitySold() {
        return quantitySold;
    }

    // Method - return user id who recorded sale
    public long getSoldByUserId() {
        return soldByUserId;
    }

    // Method - return timestamp
    public String getCreatedAt() {
        return createdAt;
    }
}
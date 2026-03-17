/*
 * Purpose:
 * - represents reorder alert information for a product
 * 
 * Function:
 * - stores product details, stock levels, reorder threshold, and alert status
 * - supports reorder review screen and future reporting
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public class ReorderAlert {
    private final long productId; // product id
    private final String productName; // product name
    private final String category; // product category
    private final int quantityOnHand; // current stock level
    private final int reorderThreshold; // threshold that triggers alert
    private final String status; // alert status text

    // Method - construct reorder alert object
    public ReorderAlert(long productId, String productName, String category, int quantityOnHand,
                        int reorderThreshold, String status) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantityOnHand = quantityOnHand;
        this.reorderThreshold = reorderThreshold;
        this.status = status;
    }

    // Method - return product id
    public long getProductId() {
        return productId;
    }

    // Method - return product name
    public String getProductName() {
        return productName;
    }

    // Method - return category
    public String getCategory() {
        return category;
    }

    // Method - return quantity on hand
    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    // Method - return reorder threshold
    public int getReorderThreshold() {
        return reorderThreshold;
    }

    // Method - return alert status
    public String getStatus() {
        return status;
    }
}
/*
 * Purpose:
 * - represents a product record in the inventory catalog
 * 
 * Function:
 * - stores core product identity and inventory tracking values
 * - provides product data to product management, sales, and reorder logic
 * - reflects an inventory-only design, so no price field is included
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public class Product {
    private final long id; // unique product id from database
    private final String name; // product display name
    private final String category; // product category such as drink, supplement, apparel
    private final int quantityOnHand; // current quantity available
    private final int reorderThreshold; // threshold for low stock / reorder logic
    private final boolean active; // indicates whether product is active in catalog

    // Method - construct product domain object
    public Product(long id, String name, String category, int quantityOnHand, int reorderThreshold, boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantityOnHand = quantityOnHand;
        this.reorderThreshold = reorderThreshold;
        this.active = active;
    }

    // Method - return product id
    public long getId() {
        return id;
    }

    // Method - return product name
    public String getName() {
        return name;
    }

    // Method - return product category
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

    // Method - return active status
    public boolean isActive() {
        return active;
    }
}
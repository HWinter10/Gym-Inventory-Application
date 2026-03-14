/*
 * Purpose:
 * - business logic for inventory adjustment workflow
 * 
 * Function:
 * - returns active categories and products for adjustment entry
 * - validates adjustment input and required reason code
 * - updates product quantity_on_hand
 * - prevents negative inventory values
 * - records adjustment in inventory_adjustments table
 * - records audit log event for traceability
 * 
 * Dependencies:
 * - Database connection helper
 * - ProductRepository for category/product lookup
 * - InventoryAdjustmentRepository for adjustment persistence
 * - AuditService for traceability
 * - SessionManager for current user
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.Database;
import com.hwinterton.gyminventory.data.InventoryAdjustmentRepository;
import com.hwinterton.gyminventory.data.ProductRepository;
import com.hwinterton.gyminventory.domain.AdjustmentReason;
import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class InventoryAdjustmentService {

    private final ProductRepository productRepository = new ProductRepository(); // product lookup access
    private final InventoryAdjustmentRepository adjustmentRepository = new InventoryAdjustmentRepository(); // adjustment persistence access
    private final AuditService auditService = new AuditService(); // audit logging

    // Method - return active categories for adjustment screen
    public List<String> listCategories() {
        requireLoggedInUser();
        return productRepository.listActiveCategories();
    }

    // Method - return active products for selected category
    public List<Product> listProductsByCategory(String category) {
        requireLoggedInUser();

        String cleanedCategory = category == null ? "" : category.trim();
        if (cleanedCategory.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }

        return productRepository.listActiveProductsByCategory(cleanedCategory);
    }

    // Method - apply signed inventory adjustment and record traceability data
    public void recordAdjustment(long productId, String direction, int amount, AdjustmentReason reason, String notes) {
        User current = requireLoggedInUser();

        if (productId <= 0) {
            throw new IllegalArgumentException("A valid product must be selected.");
        }

        String cleanedDirection = direction == null ? "" : direction.trim();
        if (cleanedDirection.isBlank()) {
            throw new IllegalArgumentException("Adjustment direction is required.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Adjustment amount must be greater than zero.");
        }

        if (reason == null) {
            throw new IllegalArgumentException("Reason code is required.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Selected product was not found."));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Selected product is inactive.");
        }

        int signedChange;
        if (cleanedDirection.equalsIgnoreCase("Increase")) {
            signedChange = amount;
        } else if (cleanedDirection.equalsIgnoreCase("Decrease")) {
            signedChange = -amount;
        } else {
            throw new IllegalArgumentException("Invalid adjustment direction.");
        }

        String cleanedNotes = notes == null ? "" : notes.trim();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // begin transaction

            String updateSql = """
                    UPDATE products
                    SET quantity_on_hand = quantity_on_hand + ?
                    WHERE id = ? AND active = 1 AND quantity_on_hand + ? >= 0;
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, signedChange);
                ps.setLong(2, productId);
                ps.setInt(3, signedChange);

                int updatedRows = ps.executeUpdate();

                if (updatedRows != 1) {
                    conn.rollback(); // undo partial update if adjustment cannot be applied
                    throw new IllegalArgumentException("Adjustment would make inventory negative or product is unavailable.");
                }
            }

            adjustmentRepository.insertAdjustment(conn, productId, signedChange, reason.name(), cleanedNotes, current.getId());
            conn.commit(); // commit product update and adjustment record together

            auditService.log(current.getId(), "INVENTORY_ADJUSTMENT",
                    "product_id=" + productId +
                    " product_name=" + product.getName() +
                    " category=" + product.getCategory() +
                    " quantity_change=" + signedChange +
                    " reason_code=" + reason.name() +
                    " notes=" + cleanedNotes);

        } catch (IllegalArgumentException e) { // rethrow validation or business rule failure
            throw e;
        } catch (Exception e) { // adjustment workflow failed
            throw new RuntimeException("Failed to record inventory adjustment", e);
        }
    }

    // Method - require authenticated session user
    private User requireLoggedInUser() {
        User user = SessionManager.getUser();
        if (user == null) {
            throw new IllegalStateException("No user is logged in.");
        }
        return user;
    }
}
/*
 * Purpose:
 * - business logic for sales entry workflow
 * 
 * Function:
 * - returns active categories and products for quick sales entry
 * - validates submitted sale quantity
 * - subtracts sold quantity from quantity_on_hand
 * - prevents negative inventory values
 * - records sale in sales table
 * - records sale event in audit log
 * 
 * Dependencies:
 * - Database connection helper
 * - ProductRepository for category/product lookup
 * - SalesRepository for sale persistence
 * - AuditService for traceability
 * - SessionManager for current user
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.Database;
import com.hwinterton.gyminventory.data.ProductRepository;
import com.hwinterton.gyminventory.data.SalesRepository;
import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class SalesService {

    private final ProductRepository productRepository = new ProductRepository(); // product lookup access
    private final SalesRepository salesRepository = new SalesRepository(); // sale persistence access
    private final AuditService auditService = new AuditService(); // audit logging

    // Method - return active categories for sales entry screen
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

    // Method - record sale and reduce quantity on hand
    public void recordSale(long productId, int quantitySold) {
        User current = requireLoggedInUser();

        if (productId <= 0) {
            throw new IllegalArgumentException("A valid product must be selected.");
        }

        if (quantitySold <= 0) {
            throw new IllegalArgumentException("Quantity sold must be greater than zero.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Selected product was not found."));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Selected product is inactive.");
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            String updateSql = """
                    UPDATE products
                    SET quantity_on_hand = quantity_on_hand - ?
                    WHERE id = ? AND active = 1 AND quantity_on_hand >= ?;
                    """;

            // update inventory only when enough stock is available
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, quantitySold);
                ps.setLong(2, productId);
                ps.setInt(3, quantitySold);

                int updatedRows = ps.executeUpdate();

                if (updatedRows != 1) {
                    conn.rollback();
                    throw new IllegalArgumentException("Sale would make inventory negative or product is unavailable.");
                }
            }

            // record sale within the same transaction
            salesRepository.insertSale(conn, productId, quantitySold, current.getId());
            conn.commit();

            auditService.log(current.getId(), "RECORD_SALE",
                    "product_id=" + productId +
                    " product_name=" + product.getName() +
                    " category=" + product.getCategory() +
                    " quantity_sold=" + quantitySold);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) { // sale workflow failed
            throw new RuntimeException("Failed to record sale", e);
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
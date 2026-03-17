/*
 * Purpose:
 * - business logic for product catalog management
 * 
 * Function:
 * - validates product input
 * - restricts product management to owners and managers
 * - creates and updates products
 * - returns product list for UI
 * - records audit log entries for product changes
 * 
 * Dependencies:
 * - ProductRepository for persistence
 * - AuthorizationService and SessionManager for permission checks
 * - AuditService for logging sensitive actions
 * - Product domain object
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.ProductRepository;
import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.AuthorizationService;
import com.hwinterton.gyminventory.security.SessionManager;

import java.util.List;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository(); // product persistence access
    private final AuditService auditService = new AuditService(); // audit logging

    // Method - return product list for management UI
    public List<Product> listProducts() {
        User current = requireLoggedInUser();
        AuthorizationService.require(AuthorizationService.canManageProducts(current));
        return productRepository.listProducts();
    }

    // Method - create product after validation and authorization
    public void createProduct(String name, String category, int quantityOnHand, int reorderThreshold, boolean active) {
        User current = requireLoggedInUser();
        AuthorizationService.require(AuthorizationService.canManageProducts(current));

        String cleanedName = validateName(name);
        String cleanedCategory = validateCategory(category);
        validateNumericFields(quantityOnHand, reorderThreshold);

        long newProductId = productRepository.insertProduct(
                cleanedName,
                cleanedCategory,
                quantityOnHand,
                reorderThreshold,
                active
        );

        auditService.log(current.getId(), "CREATE_PRODUCT",
                "target_product_id=" + newProductId +
                " name=" + cleanedName +
                " category=" + cleanedCategory +
                " quantity_on_hand=" + quantityOnHand +
                " reorder_threshold=" + reorderThreshold +
                " active=" + active);
    }

    // Method - update product after validation and authorization
    public void updateProduct(long productId, String name, String category, int quantityOnHand, int reorderThreshold, boolean active) {
        User current = requireLoggedInUser();
        AuthorizationService.require(AuthorizationService.canManageProducts(current));

        if (productId <= 0) {
            throw new IllegalArgumentException("Valid product id is required.");
        }

        String cleanedName = validateName(name);
        String cleanedCategory = validateCategory(category);
        validateNumericFields(quantityOnHand, reorderThreshold);

        productRepository.updateProduct(
                productId,
                cleanedName,
                cleanedCategory,
                quantityOnHand,
                reorderThreshold,
                active
        );

        auditService.log(current.getId(), "UPDATE_PRODUCT",
                "target_product_id=" + productId +
                " name=" + cleanedName +
                " category=" + cleanedCategory +
                " quantity_on_hand=" + quantityOnHand +
                " reorder_threshold=" + reorderThreshold +
                " active=" + active);
    }

    // Method - validate and normalize product name
    private String validateName(String name) {
        String cleaned = name == null ? "" : name.trim();
        if (cleaned.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        return cleaned;
    }

    // Method - validate and normalize category
    private String validateCategory(String category) {
        String cleaned = category == null ? "" : category.trim();
        if (cleaned.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        return cleaned;
    }

    // Method - validate numeric product fields
    private void validateNumericFields(int quantityOnHand, int reorderThreshold) {
        if (quantityOnHand < 0) {
            throw new IllegalArgumentException("Quantity on hand cannot be negative.");
        }
        if (reorderThreshold < 0) {
            throw new IllegalArgumentException("Reorder threshold cannot be negative.");
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

/*
 * Purpose:
 * - business logic for reorder alert workflow
 * 
 * Function:
 * - returns products at or below reorder threshold
 * - calculates alert status
 * - records audit log when reorder alerts are reviewed
 * 
 * Dependencies:
 * - ProductRepository for product lookup
 * - AuditService for traceability
 * - SessionManager for current user
 */

package com.hwinterton.gyminventory.service;

import com.hwinterton.gyminventory.data.ProductRepository;
import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.domain.ReorderAlert;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ReorderService {

    private final ProductRepository productRepository = new ProductRepository(); // product lookup access
    private final AuditService auditService = new AuditService(); // audit logging

    // Method - return reorder alerts for products at or below threshold
    public List<ReorderAlert> getReorderAlerts() {
        User current = requireLoggedInUser();

        List<Product> products = productRepository.listProductsNeedingReorder();
        List<ReorderAlert> alerts = new ArrayList<>();

        for (Product product : products) {
            int quantityOnHand = product.getQuantityOnHand();
            int threshold = product.getReorderThreshold();

            String status;
            if (quantityOnHand == 0) {
                status = "REORDER NOW";
            } else if (quantityOnHand < threshold) {
                status = "LOW";
            } else {
                status = "AT THRESHOLD";
            }

            alerts.add(new ReorderAlert(
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    quantityOnHand,
                    threshold,
                    status
            ));
        }

        auditService.log(current.getId(), "VIEW_REORDER_ALERTS",
                "alert_count=" + alerts.size());

        return alerts;
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
/*
 * Purpose:
 * - database access for inventory adjustment records
 * 
 * Function:
 * - inserts adjustment records using an existing transaction connection
 * 
 * Dependencies:
 * - Database connection helper
 */

package com.hwinterton.gyminventory.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InventoryAdjustmentRepository {

    // Method - insert inventory adjustment record using existing transaction connection
    public void insertAdjustment(Connection conn, long productId, int quantityChange, String reasonCode, String notes, long adjustedByUserId) {
        String sql = """
                INSERT INTO inventory_adjustments(product_id, quantity_change, reason_code, notes, adjusted_by_user_id)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setInt(2, quantityChange);
            ps.setString(3, reasonCode);
            ps.setString(4, notes);
            ps.setLong(5, adjustedByUserId);
            ps.executeUpdate();

        } catch (Exception e) { // adjustment insert failed
            throw new RuntimeException("Failed to insert inventory adjustment", e);
        }
    }
}
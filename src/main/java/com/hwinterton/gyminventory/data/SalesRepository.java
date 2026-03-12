/*
 * Purpose:
 * - database access for recorded sales
 * 
 * Function:
 * - insert sale rows after successful inventory deduction
 * 
 * Dependencies:
 * - Database connection helper
 */

package com.hwinterton.gyminventory.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SalesRepository {

    // Method - insert sale record using existing transaction connection
    public void insertSale(Connection conn, long productId, int quantitySold, long soldByUserId) {
        String sql = """
                INSERT INTO sales(product_id, quantity_sold, sold_by_user_id)
                VALUES(?, ?, ?);
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setInt(2, quantitySold);
            ps.setLong(3, soldByUserId);
            ps.executeUpdate();

        } catch (Exception e) { // sale insert failed
            throw new RuntimeException("Failed to insert sale", e);
        }
    }
}
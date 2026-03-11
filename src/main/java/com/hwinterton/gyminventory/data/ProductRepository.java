/*
 * Purpose:
 * - handles database access for product catalog records
 * 
 * Function:
 * - inserts new product rows into the products table
 * - retrieves product records for the product management screen
 * - updates existing product records
 * - finds a product by id when needed
 * 
 * Dependencies:
 * - Database connection helper
 * - Product domain object
 */

package com.hwinterton.gyminventory.data;

import com.hwinterton.gyminventory.domain.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    // Method - insert new product and return generated id
    public long insertProduct(String name, String category, int quantityOnHand, int reorderThreshold, boolean active) {
        String sql = """
                INSERT INTO products(name, category, quantity_on_hand, reorder_threshold, active)
                VALUES(?, ?, ?, ?, ?);
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            // set values for new product row
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantityOnHand);
            ps.setInt(4, reorderThreshold);
            ps.setInt(5, active ? 1 : 0);

            // execute insert statement
            ps.executeUpdate();

            // return generated product id
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }

            // fail if insert succeeded but no id was returned
            throw new RuntimeException("Product insert succeeded but no generated id returned.");

        } catch (Exception e) { // product insert failure
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    // Method - return all products ordered by name
    public List<Product> listProducts() {
        String sql = """
                SELECT id, name, category, quantity_on_hand, reorder_threshold, active
                FROM products
                ORDER BY name;
                """;

        List<Product> products = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // build Product object for each returned row
            while (rs.next()) {
                products.add(buildProduct(rs));
            }

            return products;

        } catch (Exception e) { // product list query failure
            throw new RuntimeException("Failed to list products", e);
        }
    }

    // Method - return product for specific id if found
    public Optional<Product> findById(long productId) {
        String sql = """
                SELECT id, name, category, quantity_on_hand, reorder_threshold, active
                FROM products
                WHERE id = ?;
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // set target product id
            ps.setLong(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                // return empty if no matching product found
                if (!rs.next()) return Optional.empty();

                // return mapped product if found
                return Optional.of(buildProduct(rs));
            }

        } catch (Exception e) { // product lookup failure
            throw new RuntimeException("Failed to find product", e);
        }
    }

    // Method - update existing product record
    public void updateProduct(long productId, String name, String category, int quantityOnHand, int reorderThreshold, boolean active) {
        String sql = """
                UPDATE products
                SET name = ?, category = ?, quantity_on_hand = ?, reorder_threshold = ?, active = ?
                WHERE id = ?;
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // set updated values for target product row
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantityOnHand);
            ps.setInt(4, reorderThreshold);
            ps.setInt(5, active ? 1 : 0);
            ps.setLong(6, productId);

            // execute update statement
            ps.executeUpdate();

        } catch (Exception e) { // product update failure
            throw new RuntimeException("Failed to update product", e);
        }
    }

    // Method - build Product object from query result row
    private Product buildProduct(ResultSet rs) throws Exception {
        // read product fields from current result row
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        int quantityOnHand = rs.getInt("quantity_on_hand");
        int reorderThreshold = rs.getInt("reorder_threshold");
        boolean active = rs.getInt("active") == 1;

        // return mapped domain product
        return new Product(id, name, category, quantityOnHand, reorderThreshold, active);
    }
}
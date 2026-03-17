/*
 * Purpose:
 * - database access for product catalog records
 * 
 * Function:
 * - insert new products
 * - list products for management screen
 * - update existing product records
 * - find product by id when needed
 * - return categories and active products for sales entry
 * - return products that need reorder attention
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

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantityOnHand);
            ps.setInt(4, reorderThreshold);
            ps.setInt(5, active ? 1 : 0);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }

            throw new RuntimeException("Product insert succeeded but no generated id returned.");

        } catch (Exception e) {
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

            while (rs.next()) {
                products.add(buildProduct(rs));
            }

            return products;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list products", e);
        }
    }

    // Method - return all distinct active categories ordered alphabetically
    public List<String> listActiveCategories() {
        String sql = """
                SELECT DISTINCT category
                FROM products
                WHERE active = 1
                ORDER BY category;
                """;

        List<String> categories = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

            return categories;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list categories", e);
        }
    }

    // Method - return active products in category ordered alphabetically
    public List<Product> listActiveProductsByCategory(String category) {
        String sql = """
                SELECT id, name, category, quantity_on_hand, reorder_threshold, active
                FROM products
                WHERE active = 1 AND category = ?
                ORDER BY name;
                """;

        List<Product> products = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(buildProduct(rs));
                }
            }

            return products;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list products by category", e);
        }
    }

    // Method - return active products at or below reorder threshold
    public List<Product> listProductsNeedingReorder() {
        String sql = """
                SELECT id, name, category, quantity_on_hand, reorder_threshold, active
                FROM products
                WHERE active = 1 AND quantity_on_hand <= reorder_threshold
                ORDER BY category, name;
                """;

        List<Product> products = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(buildProduct(rs));
            }

            return products;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list products needing reorder", e);
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

            ps.setLong(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(buildProduct(rs));
            }

        } catch (Exception e) {
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

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantityOnHand);
            ps.setInt(4, reorderThreshold);
            ps.setInt(5, active ? 1 : 0);
            ps.setLong(6, productId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    // Method - build Product object from query result row
    private Product buildProduct(ResultSet rs) throws Exception {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        int quantityOnHand = rs.getInt("quantity_on_hand");
        int reorderThreshold = rs.getInt("reorder_threshold");
        boolean active = rs.getInt("active") == 1;

        return new Product(id, name, category, quantityOnHand, reorderThreshold, active);
    }
}
package com.yourname.stockwise.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Product;

public class ProductDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Luyolo@041125";

    // In-memory product cache (instance-level now)
    private final List<Product> products = new ArrayList<>();

    public ProductDAO() {
        // Create products table if it doesn't exist
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "quantity INT, " +
                "threshold INT, " +
                "unitPrice DOUBLE)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Load products from DB into memory on DAO initialization
            loadProductsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load products from DB into in-memory list
    public void loadProductsFromDB() {
        products.clear();
        String sql = "SELECT * FROM products";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getInt("threshold"),
                        rs.getDouble("unitPrice")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all products (from memory)
    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Return copy to prevent external modification
    }

    // Add a product to DB and memory
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (id, name, quantity, threshold, unitPrice) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setInt(4, product.getThreshold());
            pstmt.setDouble(5, product.getUnitPrice());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                products.add(product); // Add to in-memory cache
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update product in DB and memory
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, quantity=?, threshold=?, unitPrice=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setInt(3, product.getThreshold());
            pstmt.setDouble(4, product.getUnitPrice());
            pstmt.setString(5, product.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Update in-memory product
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getId().equals(product.getId())) {
                        products.set(i, product);
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete product from DB and memory
    public boolean deleteProduct(String productId) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                products.removeIf(p -> p.getId().equals(productId));
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

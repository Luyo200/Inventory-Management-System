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

/**
 * Data Access Object (DAO) class for managing Product entities.
 * Provides methods to create, read, update, delete and batch save products
 * while maintaining an in-memory cache for fast access.
 * <p>
 * Requires the environment variable STOCKWISE_DB_PASSWORD for database authentication.
 * </p>
 * 
 * @author L Mahamba
 * @version 1.0
 */
public class ProductDAO {

    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("STOCKWISE_DB_PASSWORD");

    // In-memory cache of products for faster access
    private final List<Product> products = new ArrayList<>();

    /**
     * Constructor that creates the products table if it does not exist,
     * and loads all products from the database into the in-memory cache.
     * Throws RuntimeException if the database password environment variable is not set.
     */
    public ProductDAO() {
        String password = System.getenv("STOCKWISE_DB_PASSWORD");
        if (password == null) {
            throw new RuntimeException("DB password environment variable not set");
        }
        final String DB_PASSWORD = password;

        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "quantity INT, " +
                "threshold INT, " +
                "unitPrice DOUBLE)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            loadProductsFromDB();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all products from the database into the in-memory cache.
     */
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
                        rs.getDouble("unitPrice"));
                products.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a copy of the list of all products currently cached in memory.
     *
     * @return List of products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Defensive copy to prevent modification
    }

    /**
     * Adds a new product to the database and in-memory cache.
     *
     * @param product Product to add
     * @return true if added successfully, false otherwise
     */
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
                products.add(product); // Update cache
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing product in the database and in-memory cache.
     *
     * @param product Product with updated fields
     * @return true if update was successful, false otherwise
     */
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
                // Update in-memory cache
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

    /**
     * Saves all current in-memory products to the database.
     * Performs inserts or updates depending on existence.
     */
    public void saveAllToDatabase() {
        String insertSql = "INSERT INTO products (id, name, quantity, threshold, unitPrice) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE products SET name=?, quantity=?, threshold=?, unitPrice=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (Product product : products) {
                boolean exists = false;
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?")) {
                    checkStmt.setString(1, product.getId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            exists = true;
                        }
                    }
                }

                if (exists) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, product.getName());
                        updateStmt.setInt(2, product.getQuantity());
                        updateStmt.setInt(3, product.getThreshold());
                        updateStmt.setDouble(4, product.getUnitPrice());
                        updateStmt.setString(5, product.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, product.getId());
                        insertStmt.setString(2, product.getName());
                        insertStmt.setInt(3, product.getQuantity());
                        insertStmt.setInt(4, product.getThreshold());
                        insertStmt.setDouble(5, product.getUnitPrice());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a product by its ID from the database and in-memory cache.
     *
     * @param productId ID of the product to delete
     * @return true if deletion was successful, false otherwise
     */
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

    /**
     * Retrieves all products associated with a given username.
     * Note: Assumes a "username" column exists in the products table.
     *
     * @param username Username to filter products by
     * @return List of products belonging to the user
     */
    public List<Product> getProductsByUsername(String username) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getInt("threshold"),
                            rs.getDouble("unit_price")); // note column name: unit_price here
                    product.setUsername(rs.getString("username"));
                    productList.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
}

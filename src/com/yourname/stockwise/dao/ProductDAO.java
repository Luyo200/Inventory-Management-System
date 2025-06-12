package com.yourname.stockwise.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Product;

public class ProductDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD;

    static {
        String pwd = System.getenv("STOCKWISE_DB_PASSWORD");
        if (pwd == null || pwd.isEmpty()) {
            pwd = System.getProperty("db.password");
        }
        if (pwd == null || pwd.isEmpty()) {
            throw new RuntimeException("DB password environment variable or system property not set");
        }
        DB_PASSWORD = pwd;
    }

    private final List<Product> products = new ArrayList<>();

    public ProductDAO() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createSql = "CREATE TABLE IF NOT EXISTS products (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "quantity INT, " +
                    "threshold INT, " +
                    "unit_price DOUBLE, " +
                    "username VARCHAR(100), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createSql);

            migrateProductsTableIfNeeded(conn);
            loadProductsFromDB();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void migrateProductsTableIfNeeded(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<String> columns = new ArrayList<>();
        try (ResultSet rs = meta.getColumns(null, null, "products", null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        try (Statement stmt = conn.createStatement()) {
            if (!columns.contains("username")) {
                stmt.execute("ALTER TABLE products ADD COLUMN username VARCHAR(100)");
            }
            if (!columns.contains("unit_price")) {
                stmt.execute("ALTER TABLE products ADD COLUMN unit_price DOUBLE");
            }
            if (!columns.contains("created_at")) {
                stmt.execute("ALTER TABLE products ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
            }
        }
    }

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
                        rs.getDouble("unit_price"));
                p.setUsername(rs.getString("username"));

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    p.setDateAdded(ts.toLocalDateTime());
                }

                products.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (id, name, quantity, threshold, unit_price, username, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setInt(4, product.getThreshold());
            pstmt.setDouble(5, product.getUnitPrice());
            pstmt.setString(6, product.getUsername());
            pstmt.setTimestamp(7, Timestamp.valueOf(product.getDateAdded()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                products.add(product);
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, quantity=?, threshold=?, unit_price=?, username=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setInt(3, product.getThreshold());
            pstmt.setDouble(4, product.getUnitPrice());
            pstmt.setString(5, product.getUsername());
            pstmt.setString(6, product.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
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

    public void saveAllToDatabase() {
        String insertSql = "INSERT INTO products (id, name, quantity, threshold, unit_price, username, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE products SET name=?, quantity=?, threshold=?, unit_price=?, username=? WHERE id=?";

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
                        updateStmt.setString(5, product.getUsername());
                        updateStmt.setString(6, product.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, product.getId());
                        insertStmt.setString(2, product.getName());
                        insertStmt.setInt(3, product.getQuantity());
                        insertStmt.setInt(4, product.getThreshold());
                        insertStmt.setDouble(5, product.getUnitPrice());
                        insertStmt.setString(6, product.getUsername());
                        insertStmt.setTimestamp(7, Timestamp.valueOf(product.getDateAdded()));
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public List<Product> getProductsByDate(LocalDate date) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE DATE(created_at) = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getInt("threshold"),
                            rs.getDouble("unit_price"));
                    product.setUsername(rs.getString("username"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        product.setDateAdded(ts.toLocalDateTime());
                    }

                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

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
                            rs.getDouble("unit_price"));
                    product.setUsername(rs.getString("username"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        product.setDateAdded(ts.toLocalDateTime());
                    }

                    productList.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
}

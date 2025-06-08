package com.yourname.stockwise.dao;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.model.TransactionType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Luyolo@041125";  // <-- Change this to your actual password

    public TransactionDAO() {
        // Create transactions table if not exists
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "product_id VARCHAR(50), " +
                "type VARCHAR(20), " +
                "quantity INT, " +
                "timestamp TIMESTAMP, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (id, product_id, type, quantity, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getId());
            pstmt.setString(2, transaction.getProduct().getId());
            pstmt.setString(3, transaction.getType().name());
            pstmt.setInt(4, transaction.getQuantity());
            pstmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByProductId(String productId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE product_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public boolean deleteTransaction(String transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to convert ResultSet row to Transaction object
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String productId = rs.getString("product_id");
        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        int quantity = rs.getInt("quantity");

        Timestamp ts = rs.getTimestamp("timestamp");
        LocalDateTime timestamp = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

        // TODO: Replace dummy product creation with real Product lookup if needed
        Product dummyProduct = new Product(productId, "Unknown", 0, 0, 0.0);

        return new Transaction(id, dummyProduct, type, quantity, timestamp);
    }
}

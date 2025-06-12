package com.yourname.stockwise.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.model.TransactionType;

/**
 * Data Access Object (DAO) class for managing transactions in the StockWise application.
 * It handles CRUD operations related to Transaction entities and maintains referential integrity 
 * with the Product table.
 * 
 * Requires the environment variable STOCKWISE_DB_PASSWORD for database authentication.
 * 
 * @author L Mahamba
 * @version 1.0
 */
public class TransactionDAO {

    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("STOCKWISE_DB_PASSWORD");

    /**
     * Constructor for TransactionDAO.
     * Ensures the "transactions" table exists in the database, creating it if necessary.
     * The transactions table enforces a foreign key constraint on products.id.
     * Throws a RuntimeException if the DB password environment variable is not set.
     */
    public TransactionDAO() {
        String password = System.getenv("STOCKWISE_DB_PASSWORD");
        if (password == null) {
            throw new RuntimeException("DB password environment variable not set");
        }
        final String DB_PASSWORD = password;

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

    /**
     * Inserts a new transaction record into the database.
     *
     * @param transaction The Transaction object to add
     * @return true if the insertion was successful, false otherwise
     */
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

    /**
     * Retrieves all transactions from the database.
     *
     * @return List of all Transaction objects
     */
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

    /**
     * Retrieves all transactions for a specific product by its ID.
     *
     * @param productId The ID of the product
     * @return List of Transaction objects associated with the product
     */
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

    /**
     * Deletes a transaction record by its ID.
     *
     * @param transactionId The ID of the transaction to delete
     * @return true if the deletion was successful, false otherwise
     */
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

    /**
     * Helper method to convert a ResultSet row into a Transaction object.
     * 
     * NOTE: Since there is no ProductDAO or getProductById method,
     * this method creates a dummy Product object with unknown details.
     * You can later replace this with a real product lookup if you add a ProductDAO.
     *
     * @param rs ResultSet pointing to the current row
     * @return Transaction object mapped from the row
     * @throws SQLException if any SQL error occurs
     */
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String productId = rs.getString("product_id");
        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        int quantity = rs.getInt("quantity");

        Timestamp ts = rs.getTimestamp("timestamp");
        LocalDateTime timestamp = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();

        // Dummy product with only ID, name as "Unknown" and default values for other fields
        Product dummyProduct = new Product(productId, "Unknown", 0, 0, 0.0);

        return new Transaction(id, dummyProduct, type, quantity, timestamp);
    }
    /**
     * Retrieves all transactions that occurred on the specified date.
     * Only the date part of the timestamp is considered (ignores time).
     *
     * @param date the LocalDate to filter transactions by
     * @return a list of transactions that occurred on that date
     */
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE DATE(timestamp) = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs)); // uses your existing mapping method
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching transactions for date: " + date);
            e.printStackTrace();
        }

        return transactions;
    }

    
}

package com.yourname.stockwise.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Supplier;

/**
 * Data Access Object (DAO) class for managing supplier data in the StockWise application.
 * It handles database operations such as create, read, update, and delete (CRUD)
 * for the Supplier entity.
 * 
 * Uses an in-memory list as a cache to reduce repetitive database access.
 * 
 * Environment variable STOCKWISE_DB_PASSWORD must be set to connect to the database.
 * 
 * @author L Mahamba
 * @version 1.0
 */
public class SupplierDAO {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("STOCKWISE_DB_PASSWORD");

    // In-memory supplier cache
    private final List<Supplier> suppliers = new ArrayList<>();

    /**
     * Constructs a SupplierDAO and initializes the suppliers table if it doesn't exist.
     * Also loads existing suppliers from the database into memory.
     */
    public SupplierDAO() {
        String password = System.getenv("STOCKWISE_DB_PASSWORD");
        if (password == null) {
            throw new RuntimeException("DB password environment variable not set");
        }

        // SQL to create suppliers table if it does not exist
        String sql = "CREATE TABLE IF NOT EXISTS suppliers (" +
                     "id VARCHAR(50) PRIMARY KEY, " +
                     "name VARCHAR(255), " +
                     "email VARCHAR(255), " +
                     "phone VARCHAR(50), " +
                     "address VARCHAR(255))";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, password);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql); // Create table if not exists
            loadSuppliersFromDB(); // Load existing suppliers into memory

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all suppliers from the database and updates the in-memory list.
     */
    private void loadSuppliersFromDB() {
        suppliers.clear();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a supplier to the database and in-memory list.
     *
     * @param supplier the Supplier object to add
     * @return true if the supplier was added successfully, false otherwise
     */
    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (id, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getId());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getEmail());
            pstmt.setString(4, supplier.getPhone());
            pstmt.setString(5, supplier.getAddress());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                suppliers.add(supplier); // Add to in-memory cache
                System.out.println("Supplier added to DB: " + supplier.getId());
                return true;
            } else {
                System.err.println("Insert failed: no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + supplier.getId());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a list of all suppliers currently in memory.
     * Note: To get the latest from DB, refresh the list manually.
     *
     * @return a list of all Supplier objects
     */
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers);
    }

    /**
     * Retrieves a supplier by ID from the in-memory list.
     *
     * @param id the supplier ID
     * @return the Supplier object, or null if not found
     */
    public Supplier getSupplierById(String id) {
        for (Supplier s : suppliers) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Updates an existing supplier in the database and in-memory list.
     *
     * @param supplier the Supplier object with updated details
     * @return true if update was successful, false otherwise
     */
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name=?, email=?, phone=?, address=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getEmail());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getAddress());
            pstmt.setString(5, supplier.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Update in-memory list
                for (int i = 0; i < suppliers.size(); i++) {
                    if (suppliers.get(i).getId().equals(supplier.getId())) {
                        suppliers.set(i, supplier);
                        break;
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + supplier.getId());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a supplier from the database and removes it from the in-memory list.
     *
     * @param id the supplier ID
     * @return true if the supplier was successfully deleted, false otherwise
     */
    public boolean deleteSupplier(String id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                suppliers.removeIf(s -> s.getId().equals(id));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + id);
            e.printStackTrace();
        }

        return false;
    }
}

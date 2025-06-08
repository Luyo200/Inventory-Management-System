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

public class SupplierDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Luyolo@041125"; // Replace with your actual password

    // In-memory supplier cache
    private final List<Supplier> suppliers = new ArrayList<>();

    public SupplierDAO() {
        // Create suppliers table if it doesn't exist
        String sql = "CREATE TABLE IF NOT EXISTS suppliers (" +
                     "id VARCHAR(50) PRIMARY KEY, " +
                     "name VARCHAR(255), " +
                     "email VARCHAR(255), " +
                     "phone VARCHAR(50), " +
                     "address VARCHAR(255))";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql); // Ensure the table exists
            loadSuppliersFromDB(); // Load existing suppliers from DB into memory

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Load all suppliers from the database into memory
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
    // Add a supplier to the database and memory
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
                suppliers.add(supplier); // Add to in-memory list
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

    // Return all suppliers (reloads from DB to ensure freshness)
    public List<Supplier> getAllSuppliers() {
        //loadSuppliersFromDB(); // Always refresh from DB
        return new ArrayList<>(suppliers);
    }

    // Get a supplier by ID from in-memory cache
    public Supplier getSupplierById(String id) {
        for (Supplier s : suppliers) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Update supplier in DB and memory
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

    // Delete supplier from DB and memory
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

package com.yourname.stockwise.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Supplier;

/**
 * DAO class for managing suppliers with created_at date tracking.
 */
public class SupplierDAO {

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

    private final List<Supplier> suppliers = new ArrayList<>();

    public SupplierDAO() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createSql = "CREATE TABLE IF NOT EXISTS suppliers (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "phone VARCHAR(50), " +
                    "address VARCHAR(255), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createSql);

            migrateSuppliersTableIfNeeded(conn);
            loadSuppliersFromDB();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing SupplierDAO", e);
        }
    }

    private void migrateSuppliersTableIfNeeded(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<String> columns = new ArrayList<>();
        try (ResultSet rs = meta.getColumns(null, null, "suppliers", null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        try (Statement stmt = conn.createStatement()) {
            if (!columns.contains("created_at")) {
                stmt.execute("ALTER TABLE suppliers ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
            }
            // Add other migrations if necessary here
        }
    }

    public void loadSuppliersFromDB() {
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
                        rs.getString("address"),
                        null // default created_at to null, then override if available
                );

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    supplier.setDateAdded(ts.toLocalDateTime());
                }

                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers);
    }

    public Supplier getSupplierById(String id) {
        return suppliers.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (id, name, email, phone, address, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getId());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getEmail());
            pstmt.setString(4, supplier.getPhone());
            pstmt.setString(5, supplier.getAddress());
            pstmt.setTimestamp(6, supplier.getDateAdded() != null ? Timestamp.valueOf(supplier.getDateAdded()) : null);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                suppliers.add(supplier);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + supplier.getId());
            e.printStackTrace();
            return false;
        }
    }

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
            return false;

        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + supplier.getId());
            e.printStackTrace();
            return false;
        }
    }

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
            return false;

        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + id);
            e.printStackTrace();
            return false;
        }
    }

    public List<Supplier> getSuppliersByDate(LocalDate date) {
        List<Supplier> suppliersByDate = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE DATE(created_at) = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Supplier supplier = new Supplier(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            null
                    );

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        supplier.setDateAdded(ts.toLocalDateTime());
                    }

                    suppliersByDate.add(supplier);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching suppliers for date: " + date);
            e.printStackTrace();
        }

        return suppliersByDate;
    }

    /**
     * Save all in-memory suppliers to the database.
     * For each supplier, update if exists, else insert.
     */
    public void saveAllToDatabase() {
        String insertSql = "INSERT INTO suppliers (id, name, email, phone, address, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE suppliers SET name=?, email=?, phone=?, address=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (Supplier supplier : suppliers) {
                boolean exists = false;

                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM suppliers WHERE id = ?")) {
                    checkStmt.setString(1, supplier.getId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            exists = true;
                        }
                    }
                }

                if (exists) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, supplier.getName());
                        updateStmt.setString(2, supplier.getEmail());
                        updateStmt.setString(3, supplier.getPhone());
                        updateStmt.setString(4, supplier.getAddress());
                        updateStmt.setString(5, supplier.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, supplier.getId());
                        insertStmt.setString(2, supplier.getName());
                        insertStmt.setString(3, supplier.getEmail());
                        insertStmt.setString(4, supplier.getPhone());
                        insertStmt.setString(5, supplier.getAddress());
                        insertStmt.setTimestamp(6, supplier.getDateAdded() != null ? Timestamp.valueOf(supplier.getDateAdded()) : null);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving all suppliers to database");
            e.printStackTrace();
        }
    }
}

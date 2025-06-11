package com.yourname.stockwise.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages user security for the StockWise inventory system.
 * <p>
 * Responsible for creating and migrating the users table, adding/removing accounts,
 * authenticating logins by email or username, and retrieving user information.
 * Passwords are hashed with SHA-256 before storage.
 * </p>
 *
 * <p><b>Database:</b> MySQL at jdbc:mysql://localhost:3306/stockwise</p>
 *
 * @author L Mahamba
 * @version 1.0.0
 */
public class InventorySecurityManagement {

    // JDBC connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stockwise";
    private static final String DB_USER = "root";

    /**
     * The password for the database connection, read from environment variable
     * "STOCKWISE_DB_PASSWORD". This ensures credentials are not hardcoded.
     */
    private static final String DB_PASSWORD = System.getenv("STOCKWISE_DB_PASSWORD");

    /**
     * Constructs an InventorySecurityManagement object.
     * <p>
     * Upon creation, it checks if the 'users' table exists and creates it if not.
     * Also performs migrations if required (adds missing columns, sets constraints).
     * </p>
     *
     * @throws RuntimeException if the database password environment variable is not set
     */
    public InventorySecurityManagement() {
        if (DB_PASSWORD == null) {
            throw new RuntimeException("DB password environment variable not set");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (!doesUsersTableExist(conn)) {
                createUsersTable(conn);
            } else {
                migrateUsersTableIfNeeded(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the 'users' table exists in the database.
     *
     * @param conn an open SQL connection
     * @return true if the table exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean doesUsersTableExist(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "users", new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    /**
     * Creates the 'users' table with necessary columns and constraints.
     *
     * @param conn an open SQL connection
     * @throws SQLException if a database access error occurs
     */
    private void createUsersTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE users (" +
                "email VARCHAR(255) PRIMARY KEY, " +
                "password VARCHAR(64) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "surname VARCHAR(100) NOT NULL, " +
                "phone_number VARCHAR(20), " +
                "user_name VARCHAR(100) NOT NULL UNIQUE" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Created 'users' table.");
        }
    }

    /**
     * Performs migration of the 'users' table if columns are missing.
     * <p>
     * Adds columns 'name', 'surname', 'phone_number', and 'user_name' if not present.
     * For 'user_name', migration carefully avoids UNIQUE constraint violation by:
     * 1. Adding column nullable
     * 2. Populating existing rows with unique values (email)
     * 3. Altering to NOT NULL
     * 4. Adding UNIQUE constraint
     * </p>
     *
     * @param conn an open SQL connection
     * @throws SQLException if a database access error occurs
     */
    private void migrateUsersTableIfNeeded(Connection conn) throws SQLException {
        Set<String> existingColumns = getTableColumns(conn, "users");

        if (!existingColumns.contains("name")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT '' AFTER password");
                System.out.println("Added 'name' column.");
            }
        }

        if (!existingColumns.contains("surname")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD COLUMN surname VARCHAR(100) NOT NULL DEFAULT '' AFTER name");
                System.out.println("Added 'surname' column.");
            }
        }

        if (!existingColumns.contains("phone_number")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD COLUMN phone_number VARCHAR(20) DEFAULT NULL AFTER surname");
                System.out.println("Added 'phone_number' column.");
            }
        }

        if (!existingColumns.contains("user_name")) {
            // Step 1: Add nullable user_name column without UNIQUE constraint
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD COLUMN user_name VARCHAR(100) DEFAULT NULL AFTER phone_number");
                System.out.println("Added 'user_name' column (nullable).");
            }

            // Step 2: Populate user_name with unique values (set equal to email for now)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("UPDATE users SET user_name = email WHERE user_name IS NULL");
                System.out.println("Set user_name to email for existing rows.");
            }

            // Step 3: Change user_name to NOT NULL
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users MODIFY user_name VARCHAR(100) NOT NULL");
                System.out.println("Set 'user_name' column to NOT NULL.");
            }

            // Step 4: Add UNIQUE constraint to user_name
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD UNIQUE INDEX unique_user_name (user_name)");
                System.out.println("Added UNIQUE constraint on 'user_name'.");
            }
        }
    }

    /**
     * Retrieves the list of columns present in a given table.
     *
     * @param conn      an open SQL connection
     * @param tableName the table name to inspect
     * @return a Set of lowercase column names
     * @throws SQLException if a database access error occurs
     */
    private Set<String> getTableColumns(Connection conn, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        return columns;
    }

    /**
     * Adds a new user account to the database.
     * <p>
     * Applies a business rule to allow only one account in the system (optional).
     * Password is hashed with SHA-256 before storage.
     * </p>
     *
     * @param userData user information object containing all required fields
     * @return true if account was successfully added, false otherwise
     */
    public boolean addAccount(InventorySecurityData userData) {
        if (getAccountCount() >= 1) return false; // Optional: only one account allowed

        String sql = "INSERT INTO users(email, password, name, surname, phone_number, user_name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userData.getEmail());
            pstmt.setString(2, hashPassword(userData.getPassword()));
            pstmt.setString(3, userData.getName());
            pstmt.setString(4, userData.getSurname());
            pstmt.setString(5, userData.getPhoneNumber());
            pstmt.setString(6, userData.getUserName());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a user account by email.
     *
     * @param email the email identifying the user account
     * @return true if an account was deleted, false otherwise
     */
    public boolean removeAccount(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to authenticate a user by email and password.
     *
     * @param email    the user's email
     * @param password the plaintext password to verify
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(String email, String password) {
        String sql = "SELECT password FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return storedHash.equals(hashPassword(password));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the total number of user accounts in the database.
     *
     * @return the count of user accounts
     */
    public int getAccountCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Checks if a user email already exists.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a username already exists.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE user_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a user's email by their username.
     *
     * @param username the username to look up
     * @return the corresponding email if found, or null if not found
     */
    public String getEmailByUsername(String username) {
        String sql = "SELECT email FROM users WHERE user_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the password for a given email.
     * <p>
     * Password is hashed before updating in the database.
     * </p>
     *
     * @param email       the email identifying the user
     * @param newPassword the new plaintext password to set
     */
    public void updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to login with either email or username along with password.
     * <p>
     * If userInput contains '@', treated as email; otherwise as username.
     * Returns "success" on successful login, or an error message string.
     * </p>
     *
     * @param userInput email or username
     * @param password  plaintext password
     * @return "success" if authenticated; else an error message
     */
    public String loginWithEmailOrUsername(String userInput, String password) {
        String email = userInput;

        if (!isEmail(userInput)) {
            email = getEmailByUsername(userInput);
            if (email == null) return "Username not found.";
        }

        if (!emailExists(email)) return "Email not found.";
        if (!login(email, password)) return "Incorrect password.";

        return "success";
    }

    /**
     * Simple utility to detect if input is an email (contains '@').
     *
     * @param input the string to check
     * @return true if input looks like an email address, false otherwise
     */
    private boolean isEmail(String input) {
        return input.contains("@");
    }

    /**
     * Hashes a plaintext password using SHA-256.
     *
     * @param password the plaintext password
     * @return the hex-encoded SHA-256 hash
     * @throws RuntimeException if SHA-256 algorithm is not available (very unlikely)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }

    /**
     * Retrieves all users from the database as a list of InventorySecurityData objects.
     *
     * @return list of all user accounts, empty list if none found or on error
     * @throws RuntimeException if the database password environment variable is not set
     */
    public static List<InventorySecurityData> getAllUsers() {
        if (DB_PASSWORD == null) {
            throw new RuntimeException("DB password environment variable not set");
        }

        List<InventorySecurityData> users = new ArrayList<>();
        String sql = "SELECT name, surname, phone_number, user_name, email, password FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventorySecurityData user = new InventorySecurityData(
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("phone_number"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

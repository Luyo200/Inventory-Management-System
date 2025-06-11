package com.yourname.stockwise.security;

/**
 * Represents the security-related user data for the StockWise application.
 * <p>
 * Contains user personal details and credentials required for authentication and identification.
 * </p>
 * 
 * <p>Note: Password is stored as a String here, consider hashing in production use.</p>
 * 
 * @author L Mahamba
 * @version 1.0.0
 */
public class InventorySecurityData {

    /** User's first name */
    private String name;

    /** User's surname or last name */
    private String surname;

    /** User's phone number */
    private String phoneNumber;

    /** User's chosen username */
    private String userName;

    /** User's email address */
    private String email;

    /** User's password (should be securely hashed in real applications) */
    private String password;

    /**
     * Constructs a new InventorySecurityData instance with all fields initialized.
     * 
     * @param name        the user's first name
     * @param surname     the user's last name
     * @param phoneNumber the user's phone number
     * @param userName    the user's username
     * @param email       the user's email address
     * @param password    the user's password
     */
    public InventorySecurityData(String name, String surname, String phoneNumber, String userName, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's first name.
     * 
     * @return the first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's first name.
     * 
     * @param name the first name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's surname.
     * 
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the user's surname.
     * 
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the user's phone number.
     * 
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     * 
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's username.
     * 
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's username.
     * 
     * @param userName the username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the user's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     * <p><em>Note: For security reasons, avoid exposing the password in logs or UI.</em></p>
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * <p><em>Note: Passwords should be stored hashed and salted in production systems.</em></p>
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the InventorySecurityData object,
     * hiding the actual password for security reasons.
     * 
     * @return string representation of the user data without revealing the password
     */
    @Override
    public String toString() {
        return "InventorySecurityData{" +
               "name='" + name + '\'' +
               ", surname='" + surname + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", userName='" + userName + '\'' +
               ", email='" + email + '\'' +
               ", password='******'" +  // Mask the password in output
               '}';
    }
}

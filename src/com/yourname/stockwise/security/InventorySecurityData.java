package com.yourname.stockwise.security;

public class InventorySecurityData {

	
	    private String email;
	    private String password;

	    // Constructor
	    public InventorySecurityData (String email, String password) {
	        this.email = email;
	        this.password = password;
	    }

	    // Getters and Setters
	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    // Simple login method
	    public boolean login(String inputEmail, String inputPassword) {
	        return email.equals(inputEmail) && password.equals(inputPassword);
	    }

	    // toString method
	    @Override
	    public String toString() {
	        return "SecurityDat{" +
	               "email='" + email + '\'' +
	               ", password='******'" +  // Hide actual password for security
	               '}';
	    }
	}

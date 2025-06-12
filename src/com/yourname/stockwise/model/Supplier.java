package com.yourname.stockwise.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

/**
 * Represents a supplier in the StockWise inventory system.
 * A supplier is associated with contact details and can supply multiple products.
 * Implements {@link InventoryElement} to support operations via the Visitor pattern.
 * 
 * <p>Suppliers can be used to manage procurement, product sourcing, and 
 * stock attribution.</p>
 * 
 * @author L Mahamba
 * @version 1.1.0
 */
public class Supplier implements InventoryElement {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime dateAdded; // New field for tracking when supplier was added

    // List of products the supplier provides
    private List<Product> suppliedProducts = new ArrayList<>();

    /**
     * Constructs a new Supplier with the given details.
     *
     * @param id      unique supplier ID
     * @param name    name of the supplier
     * @param email   email address
     * @param phone   phone number
     * @param address physical address
     */
    public Supplier(String id, String name, String email, String phone, String address, LocalDateTime dateAdded) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateAdded = dateAdded; // Set to current date/time
    }

    /**
     * Accepts a visitor to perform an operation on this supplier.
     *
     * @param visitor the inventory visitor
     */
    @Override
    public void accept(InventoryVisitor visitor) {
        visitor.visit(this);
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Product> getSuppliedProducts() {
        return suppliedProducts;
    }

    public void setSuppliedProducts(List<Product> suppliedProducts) {
        this.suppliedProducts = suppliedProducts;
    }

    public void addProduct(Product product) {
        suppliedProducts.add(product);
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Supplier [id=" + id + ", name=" + name + ", email=" + email +
               ", phone=" + phone + ", address=" + address +
               ", dateAdded=" + dateAdded +
               ", suppliedProducts=" + suppliedProducts + "]";
    }
}

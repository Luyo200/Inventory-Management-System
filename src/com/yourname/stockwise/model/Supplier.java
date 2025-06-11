package com.yourname.stockwise.model;

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
 * @version 1.0.0
 */
public class Supplier implements InventoryElement {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;

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
    public Supplier(String id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
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

    /**
     * @return supplier ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id supplier ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return supplier name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name supplier name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return supplier email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email supplier email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return supplier phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return supplier's physical address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address physical address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return list of products supplied by this supplier
     */
    public List<Product> getSuppliedProducts() {
        return suppliedProducts;
    }

    /**
     * Sets the list of products supplied by this supplier.
     * 
     * @param suppliedProducts list of products to set
     */
    public void setSuppliedProducts(List<Product> suppliedProducts) {
        this.suppliedProducts = suppliedProducts;
    }

    /**
     * Adds a single product to the supplier's product list.
     * 
     * @param product product to add
     */
    public void addProduct(Product product) {
        suppliedProducts.add(product);
    }

    /**
     * Returns a string representation of the supplier.
     */
    @Override
    public String toString() {
        return "Supplier [id=" + id + ", name=" + name + ", email=" + email +
               ", phone=" + phone + ", address=" + address +
               ", suppliedProducts=" + suppliedProducts + "]";
    }
}

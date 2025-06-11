package com.yourname.stockwise.model;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

/**
 * Represents a product in the inventory system.
 * Implements {@link InventoryElement} for use with the Visitor pattern.
 * 
 * A product includes details such as its ID, name, quantity in stock,
 * reorder threshold, price per unit, and the associated username
 * (e.g., who added it).
 * 
 * <p>Used throughout the StockWise system for inventory tracking,
 * stock alerts, and valuation calculations.</p>
 * 
 * @author L Mahamba
 * @version 1.0.0
 */
public class Product implements InventoryElement {

    private String id;
    private String name;
    private int quantity;     // Current stock level
    private int threshold;    // Reorder level threshold
    private double unitPrice; // Price per unit
    private String username;  // Username of the user managing this product

    /**
     * Constructs a new Product with the provided details.
     *
     * @param id         unique identifier for the product
     * @param name       name of the product
     * @param quantity   current stock level
     * @param threshold  reorder level
     * @param unitPrice  price per unit
     */
    public Product(String id, String name, int quantity, int threshold, double unitPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.unitPrice = unitPrice;
    }

    /**
     * Accepts a visitor to perform operations on this product.
     *
     * @param visitor the inventory visitor
     */
    @Override
    public void accept(InventoryVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns whether this product is currently low in stock.
     * A product is low if its quantity is less than the threshold.
     *
     * @return true if low in stock; false otherwise
     */
    public boolean isLowStock() {
        return quantity < threshold;
    }

    /**
     * Calculates the total value of this product in stock.
     * This is based on: quantity * unit price.
     *
     * @return total inventory value of the product
     */
    public double getTotalValue() {
        return quantity * unitPrice;
    }

    // Getters and setters

    /**
     * @return product ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id product ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name product name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return current quantity in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return reorder threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * @param threshold reorder level to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * @return unit price of the product
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice unit price to set
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return username associated with this product
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username username to associate
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns a string representation of the product for debugging/logging.
     */
    @Override
    public String toString() {
        return "Product [id=" + id +
               ", name=" + name +
               ", quantity=" + quantity +
               ", threshold=" + threshold +
               ", unitPrice=" + unitPrice +
               ", username=" + username + "]";
    }
}

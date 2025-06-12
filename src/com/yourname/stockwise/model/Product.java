package com.yourname.stockwise.model;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

import java.time.LocalDateTime;

/**
 * Represents a product in the inventory system.
 * Implements {@link InventoryElement} for use with the Visitor pattern.
 * 
 * A product includes details such as its ID, name, quantity in stock,
 * reorder threshold, price per unit, the associated username, and the
 * date when the product was added.
 * 
 * <p>Used throughout the StockWise system for inventory tracking,
 * stock alerts, and valuation calculations.</p>
 * 
 * @author L Mahamba
 * @version 1.1.0
 */
public class Product implements InventoryElement {

    private String id;
    private String name;
    private int quantity;     
    private int threshold;    
    private double unitPrice; 
    private String username;  
    private LocalDateTime dateAdded; // New field

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
        this.dateAdded = LocalDateTime.now(); // Automatically sets the current date and time
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
     *
     * @return true if low in stock; false otherwise
     */
    public boolean isLowStock() {
        return quantity < threshold;
    }

    /**
     * Calculates the total value of this product in stock.
     *
     * @return total inventory value of the product
     */
    public double getTotalValue() {
        return quantity * unitPrice;
    }

    // Getters and setters

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Product [id=" + id +
               ", name=" + name +
               ", quantity=" + quantity +
               ", threshold=" + threshold +
               ", unitPrice=" + unitPrice +
               ", username=" + username +
               ", dateAdded=" + dateAdded + "]";
    }
}

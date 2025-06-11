package com.yourname.stockwise.model;

import java.time.LocalDateTime;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

/**
 * Represents a stock transaction (either addition or removal of inventory).
 * Used to track inventory movement over time.
 * <p>
 * Implements {@link InventoryElement} to support operations through the Visitor pattern.
 * </p>
 * 
 * Examples include:
 * <ul>
 *   <li>Product restocking (IN)</li>
 *   <li>Product sale or removal (OUT)</li>
 * </ul>
 * 
 * @author L Mahamba
 * @version 1.0.0
 */
public class Transaction implements InventoryElement {

    private String id;
    private Product product;
    private TransactionType type;
    private int quantity;
    private LocalDateTime timestamp;

    /**
     * Constructs a transaction.
     *
     * @param id        unique transaction identifier
     * @param product   product involved in the transaction
     * @param type      type of transaction (IN or OUT)
     * @param quantity  quantity of the product moved
     * @param timestamp date and time of the transaction
     */
    public Transaction(String id, Product product, TransactionType type, int quantity, LocalDateTime timestamp) {
        this.id = id;
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters

    /**
     * @return transaction ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the product involved in the transaction
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @return the type of transaction (IN/OUT)
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @return the number of units moved
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the date and time the transaction occurred
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters

    /**
     * @param id the transaction ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @param type the transaction type to set
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Accepts a visitor to perform an operation on this transaction.
     *
     * @param visitor the visitor object
     */
    @Override
    public void accept(InventoryVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Provides a string representation for logging or debugging purposes.
     */
    @Override
    public String toString() {
        return String.format(
                "Transaction[id=%s, product=%s, type=%s, quantity=%d, timestamp=%s]",
                id, product.getName(), type, quantity, timestamp
        );
    }
}

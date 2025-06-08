package com.yourname.stockwise.model;

import java.time.LocalDateTime;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryReportVisitor;
import com.yourname.stockwise.visitor.InventoryVisitor;

public class Transaction implements InventoryElement {
    private String id;
    private Product product;
    private TransactionType type;
    private int quantity;
    private LocalDateTime timestamp; // Full date and time

    public Transaction(String id, Product product, TransactionType type, int quantity, LocalDateTime timestamp) {
        this.id = id;
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public TransactionType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: ToString override for debugging/logging
    @Override
    public String toString() {
        return String.format("Transaction[id=%s, product=%s, type=%s, quantity=%d, timestamp=%s]",
                id, product.getName(), type, quantity, timestamp);
    }

    @Override
	public void accept(InventoryVisitor visitor) {
		// TODO Auto-generated method stub
		visitor.visit(this);

	}
}

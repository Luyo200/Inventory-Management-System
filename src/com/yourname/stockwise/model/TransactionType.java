package com.yourname.stockwise.model;

/**
 * Represents the type of a transaction in the inventory system.
 * Determines how the stock quantity is affected.
 * <p>
 * Used by the {@link Transaction} class to classify inventory movements.
 * </p>
 * 
 * Types:
 * <ul>
 *     <li>{@code SALE} - Product sold to a customer (stock decreases)</li>
 *     <li>{@code RESTOCK} - Product restocked into inventory (stock increases)</li>
 *     <li>{@code RETURN} - Product returned by a customer (stock increases)</li>
 * </ul>
 * 
 * @author
 * @version 1.0.0
 */
public enum TransactionType {
    
    /**
     * A sale transaction — reduces stock.
     */
    SALE,

    /**
     * A restock transaction — increases stock.
     */
    RESTOCK,

    /**
     * A return transaction — increases stock, typically from customer returns.
     */
    RETURN
}

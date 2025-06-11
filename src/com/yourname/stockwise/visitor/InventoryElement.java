package com.yourname.stockwise.visitor;

/**
 * Interface representing an element in the inventory system that can be visited
 * by an {@link InventoryVisitor}. This is part of the Visitor design pattern,
 * allowing external operations to be performed on inventory elements without
 * modifying their structure.
 * 
 * <p>Implementing classes should define how the visitor interacts with them
 * via the {@code accept} method.</p>
 * 
 * Example:
 * <pre>
 *     public class Product implements InventoryElement {
 *         public void accept(InventoryVisitor visitor) {
 *             visitor.visit(this);
 *         }
 *     }
 * </pre>
 * 
 * @author L Mahamba
 * @version 1.0.0
 */
public interface InventoryElement {

    /**
     * Accepts a visitor, allowing it to perform some operation on this element.
     *
     * @param visitor the visitor performing operations on this element
     */
    void accept(InventoryVisitor visitor);
}

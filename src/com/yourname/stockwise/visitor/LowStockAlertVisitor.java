package com.yourname.stockwise.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;

/**
 * Visitor that detects and collects all products whose quantity is below their defined threshold.
 * 
 * <p>This class implements the {@link InventoryVisitor} interface and is typically used
 * to trigger alerts or generate reports on stock shortages.</p>
 */
public class LowStockAlertVisitor implements InventoryVisitor {

    // List to store all products considered low in stock
    private final List<Product> lowStockProducts = new ArrayList<>();

    /**
     * Visits a product and adds it to the low stock list if its quantity is below the threshold.
     *
     * @param product the {@link Product} to evaluate
     */
    @Override
    public void visit(Product product) {
        if (product != null && product.getQuantity() < product.getThreshold()) {
            lowStockProducts.add(product);
        }
    }

    /**
     * No operation. This visitor does not process {@link Supplier} objects.
     *
     * @param supplier ignored
     */
    @Override
    public void visit(Supplier supplier) {
        // Intentionally left blank
    }

    /**
     * No operation. This visitor does not process {@link Transaction} objects.
     *
     * @param transaction ignored
     */
    @Override
    public void visit(Transaction transaction) {
        // Intentionally left blank
    }

    /**
     * Returns an unmodifiable list of products that are below their stock threshold.
     *
     * @return list of low stock products
     */
    public List<Product> getLowStockProducts() {
        return Collections.unmodifiableList(lowStockProducts);
    }

    /**
     * Clears the current list of low stock products.
     * Useful for reusing the visitor on another dataset.
     */
    public void clear() {
        lowStockProducts.clear();
    }
}

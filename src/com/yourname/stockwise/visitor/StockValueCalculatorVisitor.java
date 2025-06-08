package com.yourname.stockwise.visitor;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;

/**
 * Visitor that calculates the total value of inventory based on
 * quantity and unit price of each product.
 */
public class StockValueCalculatorVisitor implements InventoryVisitor {

    private double totalValue = 0.0;

    /**
     * Visits a product and adds its total value (quantity * unit price)
     * to the cumulative total.
     *
     * @param product the product being visited
     */
    @Override
    public void visit(Product product) {
        totalValue += product.getQuantity() * product.getUnitPrice();
    }

    /**
     * This visitor does not process suppliers.
     *
     * @param supplier the supplier (ignored)
     */
    @Override
    public void visit(Supplier supplier) {
        // Not applicable
    }

    /**
     * This visitor does not process transactions.
     *
     * @param transaction the transaction (ignored)
     */
    @Override
    public void visit(Transaction transaction) {
        // Not applicable
    }

    /**
     * Returns the total calculated value of inventory.
     *
     * @return the total inventory value
     */
    public double getTotalValue() {
        return totalValue;
    }
}

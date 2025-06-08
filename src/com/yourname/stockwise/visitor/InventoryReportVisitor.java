package com.yourname.stockwise.visitor;

import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;

/**
 * Visitor implementation that generates textual summaries
 * of inventory elements: Products, Suppliers, and Transactions.
 */
public class InventoryReportVisitor implements InventoryVisitor {

    private List<String> productSummaries = new ArrayList<>();
    private List<String> supplierSummaries = new ArrayList<>();
    private List<String> transactionSummaries = new ArrayList<>();

    /**
     * Visits a product and adds its string summary to the report.
     *
     * @param product the product to summarize
     */
    @Override
    public void visit(Product product) {
        productSummaries.add(product.toString());
    }

    /**
     * Visits a supplier and adds its string summary to the report.
     *
     * @param supplier the supplier to summarize
     */
    @Override
    public void visit(Supplier supplier) {
        supplierSummaries.add(supplier.toString());
    }

    /**
     * Visits a transaction and adds its string summary to the report.
     *
     * @param transaction the transaction to summarize
     */
    @Override
    public void visit(Transaction transaction) {
        transactionSummaries.add(transaction.toString());
    }

    /** Returns summaries of all visited products. */
    public List<String> getProductSummaries() {
        return productSummaries;
    }

    /** Sets the product summaries (used in testing or pre-population). */
    public void setProductSummaries(List<String> productSummaries) {
        this.productSummaries = productSummaries;
    }

    /** Returns summaries of all visited suppliers. */
    public List<String> getSupplierSummaries() {
        return supplierSummaries;
    }

    /** Sets the supplier summaries (used in testing or pre-population). */
    public void setSupplierSummaries(List<String> supplierSummaries) {
        this.supplierSummaries = supplierSummaries;
    }

    /** Returns summaries of all visited transactions. */
    public List<String> getTransactionSummaries() {
        return transactionSummaries;
    }

    /** Sets the transaction summaries (used in testing or pre-population). */
    public void setTransactionSummaries(List<String> transactionSummaries) {
        this.transactionSummaries = transactionSummaries;
    }
}

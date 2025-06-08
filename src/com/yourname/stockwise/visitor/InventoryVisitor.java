package com.yourname.stockwise.visitor;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;

public interface InventoryVisitor {
	public void visit(Product product);

	public void visit(Supplier supplier);

	public void visit(Transaction transaction);

}

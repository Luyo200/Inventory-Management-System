package com.yourname.stockwise.visitor;

public interface InventoryElement {
	void accept(InventoryVisitor visitor);

}

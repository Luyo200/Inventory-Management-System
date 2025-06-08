package com.yourname.stockwise.model;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

public class Product implements InventoryElement {
	private String id;
	private String name;
	private int quantity;// — current stock level
	private int Threshold;

	private double unitPrice;

	/**
	 * @param id
	 * @param name
	 * @param category
	 * @param quantity
	 * @param unitPrice
	 * @param reorderLevel
	 * @param supplierId
	 * @param description
	 */

	@Override
	public void accept(InventoryVisitor visitor) {
		// TODO Auto-generated method stub
		visitor.visit(this);

	}

	/**
	 * @param id
	 * @param name
	 * @param quantity
	 * @param threshold
	 * @param unitPrice
	 */
	public Product(String id, String name, int quantity, int Threshold, double unitPrice) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.Threshold = Threshold;
		this.unitPrice = unitPrice;
	}

	public boolean isLowStock() {
		return quantity < unitPrice;

	}

	public double getTotalValue() {
		// — e.g., quantity * unitPrice
		return quantity * Threshold;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the threshold
	 */
	public int getThreshold() {
		return Threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(int threshold) {
		Threshold = threshold;
	}

	/**
	 * @return the unitPrice
	 */
	public double getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", quantity=" + quantity + ", Threshold=" + Threshold
				+ ", unitPrice=" + unitPrice + "]";
	}

}

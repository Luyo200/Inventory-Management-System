package com.yourname.stockwise.model;

import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.visitor.InventoryElement;
import com.yourname.stockwise.visitor.InventoryVisitor;

public class Supplier implements InventoryElement {
	private String id;
	private String name;
	private String email;
	private String phone;
	private String address;

	// Optional: Keep track of products they supply
	private List<Product> suppliedProducts = new ArrayList<>();

	/**
	 * @param id
	 * @param name
	 * @param email
	 * @param phone
	 * @param address
	 * @param suppliedProducts
	 */
	public Supplier(String id, String name, String email, String phone, String address) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.suppliedProducts = suppliedProducts;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the suppliedProducts
	 */
	public List<Product> getSuppliedProducts() {
		return suppliedProducts;
	}

	/**
	 * @param suppliedProducts the suppliedProducts to set
	 */
	public void setSuppliedProducts(List<Product> suppliedProducts) {
		this.suppliedProducts = suppliedProducts;
	}

	@Override
	public String toString() {
		return "Supplier [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", address="
				+ address + ", suppliedProducts=" + suppliedProducts + "]";
	}

	@Override
	public void accept(InventoryVisitor visitor) {
		// TODO Auto-generated method stub
		visitor.visit(this);

	}

	// Optional: Add a product to the supplier
	public void addProduct(Product product) {
		suppliedProducts.add(product);
	}

}

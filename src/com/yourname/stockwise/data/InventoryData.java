package com.yourname.stockwise.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yourname.stockwise.model.Product;

public class InventoryData {

	private static final List<Product> products = new ArrayList<>();

	
	public static void setProducts(List<Product> productList) {
		products.clear();
		products.addAll(productList);

	}

	public static List<Product> getAllProducts() {
		return Collections.unmodifiableList(products);

	}

	public static void addProduct(Product product) {
		products.add(product);
	}

	public static void clearProducts() {
		products.clear();
	}

	/**
	 * @return the products
	 */
	public static List<Product> getProducts() {
		return products;
	}

	public static Product findProductById(String productId) {
		for (Product p : products) {
			if (p.getId().equals(productId)) {
				return p;
			}
		}
		return null; // not found
	}


}

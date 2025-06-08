package com.yourname.stockwise.util;

import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.yourname.stockwise.data.InventoryData;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.model.TransactionType;

public class FileHandler {

	private static final String PRODUCT_FILE = "Data/Products.txt";
	private static final String SUPPLIER_FILE = "Data/Suppliers.txt";
	private static final String TRANSACTION_FILE = "Data/Transactions.txt";

	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	// ========================= PRODUCT =========================

	public static void saveProducts(List<Product> products) {
		try (FileWriter writer = new FileWriter(PRODUCT_FILE, false)) {
			for (Product p : products) {
				writer.write(String.format("%s,%s,%d,%d,%.2f%n", p.getId(), p.getName(), p.getQuantity(),
						p.getThreshold(), p.getUnitPrice()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Product> loadProducts() {
		List<Product> products = new ArrayList<>();
		File file = new File(PRODUCT_FILE);
		if (!file.exists())
			return products;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 5) {
					String id = parts[0];
					String name = parts[1];
					int quantity = Integer.parseInt(parts[2]);
					int threshold = Integer.parseInt(parts[3]);
					double unitPrice = Double.parseDouble(parts[4]);

					products.add(new Product(id, name, quantity, threshold, unitPrice));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return products;
	}

	// ========================= SUPPLIER =========================

	public static void saveSuppliers(List<Supplier> suppliers) {
		try (FileWriter writer = new FileWriter(SUPPLIER_FILE, false)) {
			for (Supplier s : suppliers) {
				writer.write(String.format("%s,%s,%s,%s,%s%n", s.getId(), s.getName(), s.getPhone(), s.getEmail(),
						s.getAddress()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Supplier> loadSuppliers() {
		List<Supplier> suppliers = new ArrayList<>();
		File file = new File(SUPPLIER_FILE);
		if (!file.exists())
			return suppliers;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 5) {
					String id = parts[0];
					String name = parts[1];
					String email = parts[2];
					String phone = parts[3];
					String address = parts[4];

					suppliers.add(new Supplier(id, name, email, phone, address));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return suppliers;
	}

	// ========================= TRANSACTION =========================

	public static void saveTransactions(List<Transaction> transactions) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_FILE))) {
			for (Transaction t : transactions) {
				String timestamp = t.getTimestamp().format(DATETIME_FORMATTER);
				writer.println(String.format("%s,%s,%s,%d,%s", t.getId(), t.getProduct().getId(), t.getType().name(),
						t.getQuantity(), timestamp));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Transaction> loadTransaction() {
		List<Transaction> transactions = new ArrayList<>();
		File file = new File(TRANSACTION_FILE);
		if (!file.exists()) {
			return transactions; // empty list if file doesn't exist
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Each line format: id,productId,type,quantity,timestamp
				String[] parts = line.split(",");
				if (parts.length == 5) {
					String id = parts[0];
					String productId = parts[1];
					String typeStr = parts[2];
					int quantity = Integer.parseInt(parts[3]);

					// ✅ Parse full datetime string using formatter
					LocalDateTime timestamp = LocalDateTime.parse(parts[4], DATETIME_FORMATTER);

					// Lookup Product object by ID
					Product product = InventoryData.findProductById(productId);
					if (product == null) {
						System.err.println("Product with ID " + productId + " not found for transaction " + id);
						continue;
					}

					// Parse TransactionType enum
					TransactionType type;
					try {
						type = TransactionType.valueOf(typeStr);
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid transaction type: " + typeStr);
						continue;
					}

					Transaction tran = new Transaction(id, product, type, quantity, timestamp);
					transactions.add(tran);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return transactions;
	}
}

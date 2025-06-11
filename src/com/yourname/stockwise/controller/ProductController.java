package com.yourname.stockwise.controller;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.dao.ProductDAO;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.util.AlertHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller class to handle product-related UI interactions.
 * Displays forms and handles actions for adding new products.
 * 
 * Author: L Mahamba
 * Version: 1.0
 */
public class ProductController {

    private final ProductDAO productDAO = new ProductDAO();
    private final InventoryApp app = new InventoryApp();

    /**
     * Displays the Add Product form on the provided Stage.
     * Handles form inputs, validation, and saving the product.
     *
     * @param stage The JavaFX Stage to show the form in.
     */
    public void showAddProductForm(Stage stage) {
        // Generate unique product ID
        String autoId = generateNextProductId(productDAO.getAllProducts());

        Label titleLabel = new Label("➕ Add New Product");
        titleLabel.setFont(new Font("Arial", 20));

        TextField idField = new TextField(autoId);
        idField.setPromptText("Product ID");
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #333;");

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Threshold");

        TextField priceField = new TextField();
        priceField.setPromptText("Unit Price");

        Button saveBtn = new Button("💾 Save");
        Button cancelBtn = new Button("⬅ Back");

        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
                    "Are you sure you want to save this product?");
            if (!confirmed) return;

            // Validate inputs before parsing
            if (nameField.getText().isBlank() || qtyField.getText().isBlank() ||
                thresholdField.getText().isBlank() || priceField.getText().isBlank()) {
                AlertHelper.showError("Missing Fields", "Please fill in all fields.");
                return;
            }

            try {
                String id = idField.getText();
                String name = nameField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                int threshold = Integer.parseInt(thresholdField.getText());
                double price = Double.parseDouble(priceField.getText());

                Product newProduct = new Product(id, name, qty, threshold, price);

                boolean added = productDAO.addProduct(newProduct);
                if (added) {
                    productDAO.saveAllToDatabase();
                    AlertHelper.showInfo("Product Added", "Product added successfully!");
                    app.clearFields(idField, nameField, qtyField, thresholdField, priceField);
                    idField.setText(generateNextProductId(productDAO.getAllProducts()));
                } else {
                    AlertHelper.showError("Save Failed", "Failed to add product.");
                }

            } catch (NumberFormatException ex) {
                AlertHelper.showError("Invalid Input", "Please enter valid numeric values.");
            }
        });

        cancelBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Go Back",
                    "Are you sure you want to go back? Unsaved data will be lost.");
            if (confirmed) {
                app.showProductTable(stage);
            }
        });

        VBox form = new VBox(12, titleLabel, idField, nameField, qtyField, thresholdField, priceField, saveBtn,
                cancelBtn);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);
        form.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #e0eafc, #cfdef3);
                -fx-border-color: #4a90e2;
                -fx-border-width: 2;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                -fx-font-size: 14px;
                """);

        Scene scene = new Scene(form, 400, 480);
        stage.setScene(scene);
    }

    /**
     * Generates the next unique product ID.
     * For example: P0001, P0002, ...
     *
     * @param products List of existing products
     * @return A unique product ID not used by any existing product
     */
    private String generateNextProductId(List<Product> products) {
        Set<String> existingIds = new HashSet<>();
        for (Product p : products) {
            existingIds.add(p.getId());
        }

        int num = 1;
        while (true) {
            String candidate = "P" + String.format("%04d", num);
            if (!existingIds.contains(candidate)) {
                return candidate;
            }
            num++;
        }
    }
}

package com.yourname.stockwise.controller;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.dao.ProductDAO;
import com.yourname.stockwise.data.InventoryData;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.util.AlertHelper;
import com.yourname.stockwise.util.FileHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductController {

    

    /**
	 * Displays a form to add a new product to the inventory.
	 *
	 * @param stage the JavaFX stage to display the product form
	 */
	public void showAddProductForm(Stage stage) {
		Label titleLabel = new Label("Add New Product");

		TextField idField = new TextField();
		idField.setPromptText("Product ID");

		TextField nameField = new TextField();
		nameField.setPromptText("Product Name");

		TextField qtyField = new TextField();
		qtyField.setPromptText("Quantity");

		TextField thresholdField = new TextField();
		thresholdField.setPromptText("Threshold");

		TextField priceField = new TextField();
		priceField.setPromptText("Unit Price");

		Button saveBtn = new Button("Save");
		Button cancelBtn = new Button("Back");

		Label statusLabel = new Label();

		saveBtn.setOnAction(e -> {
			boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
					"Are you sure you want to save this product?");
			if (!confirmed)
				return;

			try {
				String id = idField.getText();
				String name = nameField.getText();
				int qty = Integer.parseInt(qtyField.getText());
				int threshold = Integer.parseInt(thresholdField.getText());
				double price = Double.parseDouble(priceField.getText());

				Product newProduct = new Product(id, name, qty, threshold, price);

				// Store the product in your inventory
				InventoryData.addProduct(newProduct);// Show success alert
				// Save the updated product list to the file
				FileHandler.saveProducts(InventoryData.getAllProducts());
				AlertHelper.showInfo("Product Added", "Product added successfully!");

				// Optionally clear fields or go back
				InventoryApp app = new InventoryApp();
				app.clearFields(idField, nameField, qtyField, thresholdField, priceField);

			} catch (NumberFormatException ex) {
				AlertHelper.showError("Invalid Input", "Please enter valid numeric values.");
			}
		});

		InventoryApp app = new InventoryApp();

		cancelBtn.setOnAction(e -> {
			boolean confirmed = AlertHelper.showConfirmation("Go Back",
					"Are you sure you want to go back? Unsaved data will be lost.");
			if (confirmed) {
				app.showProductTable(stage);
			}
		});

		VBox form = new VBox(10, titleLabel, idField, nameField, qtyField, thresholdField, priceField, saveBtn,
				cancelBtn, statusLabel);
		form.setStyle("-fx-padding: 20");

		stage.setScene(new Scene(form, 400, 400));
	}

}

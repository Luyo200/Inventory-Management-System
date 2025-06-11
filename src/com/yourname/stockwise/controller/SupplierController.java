package com.yourname.stockwise.controller;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.dao.SupplierDAO;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.util.AlertHelper;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class to manage supplier-related UI interactions.
 * Displays a form for adding new suppliers and handles form submission.
 */
public class SupplierController {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final InventoryApp app = new InventoryApp();

    /**
     * Shows the Add Supplier form on the given Stage.
     * Handles input validation, saving, and navigation.
     * 
     * @param stage The JavaFX Stage to display the form on.
     */
    public void showAddSupplierForm(Stage stage) {
        Label title = new Label("Add New Supplier");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextField idField = new TextField();
        idField.setPromptText("Supplier ID");
        styleTextField(idField);

        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");
        styleTextField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        styleTextField(phoneField);

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        styleTextField(addressField);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Back");
        styleButton(saveBtn, "#27ae60", "#1e8449"); // green colors
        styleButton(cancelBtn, "#2980b9", "#1c5980"); // blue colors

        saveBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
                    "Are you sure you want to save this supplier?");
            if (!confirmed) return;

            if (!validateInputs(idField, nameField, statusLabel)) {
                return;
            }

            try {
                Supplier supplier = new Supplier(
                        idField.getText().trim(),
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        addressField.getText().trim()
                );

                boolean success = supplierDAO.addSupplier(supplier);
                if (success) {
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // green text on success
                    statusLabel.setText("Supplier added successfully!");
                    app.clearFields(idField, nameField, emailField, phoneField, addressField);
                } else {
                    statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    statusLabel.setText("Failed to add supplier to database.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Go Back",
                    "Are you sure you want to go back? Unsaved data will be lost.");
            if (confirmed) {
                app.showSuppliers(stage);
            }
        });

        VBox form = new VBox(15, title, idField, nameField, emailField, phoneField, addressField, saveBtn, cancelBtn,
                statusLabel);
        form.setStyle("-fx-padding: 25; "
                + "-fx-background-color: #d9f0d9; " // pastel green background
                + "-fx-border-radius: 12; "
                + "-fx-background-radius: 12;");

        stage.setScene(new Scene(form, 420, 480));
    }

    // Validates mandatory input fields; shows error in statusLabel if invalid
    private boolean validateInputs(TextField idField, TextField nameField, Label statusLabel) {
        if (idField.getText().isBlank() || nameField.getText().isBlank()) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText("Supplier ID and Name are required.");
            return false;
        }
        return true;
    }

    // Helper method to style text fields uniformly
    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-padding: 8 10 8 10; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: #bdc3c7; " + // light gray border
                "-fx-font-size: 14px;");
    }

    // Helper method to style buttons uniformly with hover effect
    private void styleButton(Button btn, String baseColor, String hoverColor) {
        btn.setStyle("-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 25 10 25; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + hoverColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25 10 25; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + baseColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 25 10 25; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"));
    }
}

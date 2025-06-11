package com.yourname.stockwise.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.dao.ProductDAO;
import com.yourname.stockwise.dao.TransactionDAO;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.model.TransactionType;
import com.yourname.stockwise.util.AlertHelper;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controller class responsible for managing the transaction form UI and logic.
 * Provides functionality to add new transactions to the inventory system.
 * 
 * @author L Mahamba
 */
public class TransactionController {

    /**
     * Displays the Add New Transaction form on the given stage.
     * Allows the user to input transaction details and save them to the database.
     *
     * @param stage The JavaFX Stage on which to display the form.
     * 
     * @author L Mahamba
     */
    public void showAddTransactionForm(Stage stage) {
        // Title label with styling
        Label title = new Label("Add New Transaction");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Transaction ID input field
        TextField idField = new TextField();
        idField.setPromptText("Transaction ID");
        styleTextField(idField);

        // ComboBox to select a Product from the database
        ProductDAO productDAO = new ProductDAO();
        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.getItems().addAll(productDAO.getAllProducts());
        // Custom string converter to show product name instead of object reference
        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product == null ? "" : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return null; // Not used in this context
            }
        });
        styleComboBox(productComboBox);

        // ComboBox to select the type of transaction (RESTOCK or SALE)
        ComboBox<TransactionType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(TransactionType.values());
        styleComboBox(typeComboBox);

        // Quantity input field
        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        styleTextField(qtyField);

        // DatePicker initialized to current date for transaction date
        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleDatePicker(datePicker);

        // Input fields for hour and minute of transaction time
        TextField hourField = new TextField();
        hourField.setPromptText("Hour (0-23)");
        styleTextField(hourField);

        TextField minuteField = new TextField();
        minuteField.setPromptText("Minute (0-59)");
        styleTextField(minuteField);

        // Label to display status messages (errors, success notifications)
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Save button with green styling and event handler
        Button saveBtn = new Button("Save");
        styleButton(saveBtn, "#27ae60", "#1e8449");

        // Back/cancel button with blue styling and event handler
        Button cancelBtn = new Button("Back");
        styleButton(cancelBtn, "#2980b9", "#1c5980");

        // Save button action handler
        saveBtn.setOnAction(e -> {
            // Confirm before saving
            boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
                    "Are you sure you want to save this Transaction?");
            if (!confirmed) return;

            // Read inputs from form fields
            String transactionId = idField.getText().trim();
            Product selectedProduct = productComboBox.getValue();
            TransactionType type = typeComboBox.getValue();
            String qtyText = qtyField.getText().trim();
            LocalDate date = datePicker.getValue();
            String hourText = hourField.getText().trim();
            String minuteText = minuteField.getText().trim();

            // Validate all required fields are filled
            if (transactionId.isEmpty() || selectedProduct == null || type == null || date == null ||
                    qtyText.isEmpty() || hourText.isEmpty() || minuteText.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Please fill all required fields.");
                return;
            }

            int quantity, hour, minute;
            try {
                // Parse numeric inputs
                quantity = Integer.parseInt(qtyText);
                hour = Integer.parseInt(hourText);
                minute = Integer.parseInt(minuteText);
            } catch (NumberFormatException ex) {
                // Show error if parsing fails
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Quantity, hour, and minute must be valid numbers.");
                return;
            }

            // Validate quantity and time ranges
            if (quantity <= 0) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Quantity must be a positive number.");
                return;
            }

            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Invalid time entered.");
                return;
            }

            try {
                // Create timestamp from date and time inputs
                LocalDateTime timestamp = LocalDateTime.of(date, LocalTime.of(hour, minute));
                // Create transaction model instance
                Transaction transaction = new Transaction(transactionId, selectedProduct, type, quantity, timestamp);

                // Persist transaction in database
                TransactionDAO transactionDAO = new TransactionDAO();
                boolean success = transactionDAO.addTransaction(transaction);

                if (success) {
                    // Update product quantity based on transaction type
                    if (type == TransactionType.RESTOCK) {
                        selectedProduct.setQuantity(selectedProduct.getQuantity() + quantity);
                    } else if (type == TransactionType.SALE) {
                        selectedProduct.setQuantity(selectedProduct.getQuantity() - quantity);
                    }
                    // Save updated product back to database
                    productDAO.updateProduct(selectedProduct);

                    // Show success message
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    statusLabel.setText("Transaction added successfully!");

                    // Reset form fields for next entry
                    idField.clear();
                    qtyField.clear();
                    productComboBox.getSelectionModel().clearSelection();
                    typeComboBox.getSelectionModel().clearSelection();
                    datePicker.setValue(LocalDate.now());
                    hourField.clear();
                    minuteField.clear();
                } else {
                    // Failure saving to database
                    statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    statusLabel.setText("Failed to save transaction to the database.");
                }
            } catch (Exception ex) {
                // Catch-all for unexpected errors
                ex.printStackTrace();
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("An unexpected error occurred.");
            }
        });

        // Cancel button event handler: confirm and go back to transactions list
        InventoryApp app = new InventoryApp();
        cancelBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Go Back",
                    "Are you sure you want to go back? Unsaved data will be lost.");
            if (confirmed) {
                app.showTransactions(stage);
            }
        });

        // Layout container for form components with spacing and padding
        VBox form = new VBox(15, title, idField, productComboBox, typeComboBox, qtyField,
                datePicker, hourField, minuteField, saveBtn, cancelBtn, statusLabel);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: #e6f0fa; -fx-background-radius: 12;");

        // Set scene and show form
        stage.setScene(new Scene(form, 420, 520));
    }

    /**
     * Helper method to apply consistent styling to TextField controls.
     *
     * @param tf The TextField to style.
     * 
     * @author L Mahamba
     */
    private void styleTextField(TextField tf) {
        tf.setStyle("""
                -fx-padding: 8 12 8 12;
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-border-color: #bdc3c7;
                -fx-font-size: 14px;
                -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);
                """);
    }

    /**
     * Helper method to apply consistent styling to ComboBox controls.
     *
     * @param cb The ComboBox to style.
     * 
     * @author L Mahamba
     */
    private void styleComboBox(ComboBox<?> cb) {
        cb.setStyle("""
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-border-color: #bdc3c7;
                -fx-padding: 5 10 5 10;
                -fx-font-size: 14px;
                """);
    }

    /**
     * Helper method to apply consistent styling to DatePicker controls.
     *
     * @param dp The DatePicker to style.
     * 
     * @author L Mahamba
     */
    private void styleDatePicker(DatePicker dp) {
        dp.setStyle("""
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-border-color: #bdc3c7;
                -fx-padding: 5 10 5 10;
                -fx-font-size: 14px;
                """);
    }

    /**
     * Helper method to apply styling and hover effects to Button controls.
     *
     * @param btn       The Button to style.
     * @param baseColor The default background color of the button.
     * @param hoverColor The background color when the mouse hovers over the button.
     * 
     * @author L Mahamba
     */
    private void styleButton(Button btn, String baseColor, String hoverColor) {
        btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 25 10 25;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """, baseColor));

        // Change background color on mouse hover
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 25 10 25;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """, hoverColor)));

        // Revert background color when mouse exits
        btn.setOnMouseExited(e -> btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 25 10 25;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """, baseColor)));
    }
}

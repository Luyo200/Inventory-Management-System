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

public class TransactionController {

    public void showAddTransactionForm(Stage stage) {
        Label title = new Label("Add New Transaction");

        TextField idField = new TextField();
        idField.setPromptText("Transaction ID");

        ProductDAO productDAO = new ProductDAO();
        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.getItems().addAll(productDAO.getAllProducts());

        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product == null ? "" : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });

        ComboBox<TransactionType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(TransactionType.values());

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField hourField = new TextField();
        hourField.setPromptText("Hour (0-23)");

        TextField minuteField = new TextField();
        minuteField.setPromptText("Minute (0-59)");

        Label statusLabel = new Label();

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Back");

        saveBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
                    "Are you sure you want to save this Transaction?");
            if (!confirmed) return;

            try {
                String transactionId = idField.getText();
                Product selectedProduct = productComboBox.getValue();
                TransactionType type = typeComboBox.getValue();
                int quantity = Integer.parseInt(qtyField.getText());
                LocalDate date = datePicker.getValue();

                if (transactionId.isEmpty() || selectedProduct == null || type == null || date == null) {
                    statusLabel.setText("Please fill all required fields.");
                    return;
                }

                if (hourField.getText().isEmpty() || minuteField.getText().isEmpty()) {
                    statusLabel.setText("Please enter hour and minute.");
                    return;
                }

                int hour = Integer.parseInt(hourField.getText());
                int minute = Integer.parseInt(minuteField.getText());

                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    statusLabel.setText("Invalid time entered.");
                    return;
                }

                LocalDateTime timestamp = LocalDateTime.of(date, LocalTime.of(hour, minute));
                Transaction transaction = new Transaction(transactionId, selectedProduct, type, quantity, timestamp);

                // Save transaction to DB
                TransactionDAO transactionDAO = new TransactionDAO();
                boolean success = transactionDAO.addTransaction(transaction);

                if (success) {
                    // Update product quantity in DB
                    if (type == TransactionType.RESTOCK) {
                        selectedProduct.setQuantity(selectedProduct.getQuantity() + quantity);
                    } else if (type == TransactionType.SALE) {
                        selectedProduct.setQuantity(selectedProduct.getQuantity() - quantity);
                    }

                    // Persist updated product
                    productDAO.updateProduct(selectedProduct);

                    statusLabel.setText("Transaction added successfully!");

                    // Clear form
                    idField.clear();
                    qtyField.clear();
                    productComboBox.getSelectionModel().clearSelection();
                    typeComboBox.getSelectionModel().clearSelection();
                    datePicker.setValue(LocalDate.now());
                    hourField.clear();
                    minuteField.clear();
                } else {
                    statusLabel.setText("Failed to save transaction to the database.");
                }

            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid quantity or time input.");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("An unexpected error occurred.");
            }
        });

        InventoryApp app = new InventoryApp();
        cancelBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Go Back",
                    "Are you sure you want to go back? Unsaved data will be lost.");
            if (confirmed) {
                app.showTransactions(stage);
            }
        });

        VBox form = new VBox(10, title, idField, productComboBox, typeComboBox, qtyField,
                datePicker, hourField, minuteField, saveBtn, cancelBtn, statusLabel);
        form.setPadding(new Insets(20));

        stage.setScene(new Scene(form, 400, 500));
    }
}

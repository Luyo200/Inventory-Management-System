package com.yourname.stockwise.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.dao.SupplierDAO;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.util.AlertHelper;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SupplierController implements Initializable {

    public void showAddSupplierForm(Stage stage) {
        Label title = new Label("Add New Supplier");

        TextField idField = new TextField();
        idField.setPromptText("Supplier ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        Label statusLabel = new Label();

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Back");

        saveBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Confirm Save",
                    "Are you sure you want to save this supplier?");
            if (!confirmed) return;

            try {
                String id = idField.getText();
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();

                if (id.isEmpty() || name.isEmpty()) {
                    statusLabel.setText("ID and Name are required.");
                    return;
                }

                Supplier supplier = new Supplier(id, name, email, phone, address);
                SupplierDAO supplierDAO = new SupplierDAO();

                boolean success = supplierDAO.addSupplier(supplier);
                if (success) {
                    statusLabel.setText("Supplier added successfully!");

                    InventoryApp app = new InventoryApp();
                    app.clearFields(idField, nameField, emailField, phoneField, addressField);
                } else {
                    statusLabel.setText("Failed to add supplier to database.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        InventoryApp app = new InventoryApp();
        cancelBtn.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Go Back",
                    "Are you sure you want to go back? Unsaved data will be lost.");
            if (confirmed) {
                app.showSuppliers(stage);
            }
        });

        VBox form = new VBox(10, title, idField, nameField, emailField, phoneField, addressField, saveBtn, cancelBtn,
                statusLabel);
        form.setStyle("-fx-padding: 20");

        stage.setScene(new Scene(form, 400, 450));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No initialization required here
    }
}

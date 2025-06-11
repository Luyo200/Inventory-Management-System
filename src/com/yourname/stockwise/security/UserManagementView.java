package com.yourname.stockwise.security;

import java.util.List;

import com.yourname.stockwise.app.InventoryApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View class to manage users in the inventory system.
 * Displays a table of users with options to delete and navigate back to the dashboard.
 * 
 * @author L Mahamba
 */
public class UserManagementView {

    private final TableView<InventorySecurityData> tableView = new TableView<>();
    private final ObservableList<InventorySecurityData> userList = FXCollections.observableArrayList();

    private final InventorySecurityManagement securityManager = new InventorySecurityManagement();

    /**
     * Shows the User Management interface on the provided stage.
     * 
     * @param stage the primary stage where this scene will be set
     */
    public void show(Stage stage) {
        // Setup table columns
        TableColumn<InventorySecurityData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<InventorySecurityData, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<InventorySecurityData, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<InventorySecurityData, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<InventorySecurityData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Mask password display
        TableColumn<InventorySecurityData, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(param -> 
            javafx.beans.property.SimpleStringProperty.stringExpression(
                javafx.beans.binding.Bindings.createStringBinding(() -> "******"))
        );

        // Delete Button Column
        TableColumn<InventorySecurityData, Void> deleteCol = new TableColumn<>("Action");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setOnAction(event -> {
                    InventorySecurityData user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
                deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        tableView.getColumns().addAll(nameCol, surnameCol, phoneCol, usernameCol, emailCol, passwordCol, deleteCol);

        loadUsers();

        tableView.setItems(userList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6;");
        backBtn.setOnAction(e -> {
        	InventoryApp d = new InventoryApp();
             d.showDashboard(stage);
        });

        // Layout container for the Back button (left aligned)
        HBox buttonBox = new HBox(backBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 15));

        VBox root = new VBox(10, tableView, buttonBox);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 900, 550);
        stage.setScene(scene);
        stage.setTitle("User Management");
        stage.show();
    }

    /**
     * Loads users from the database into the table.
     */
    private void loadUsers() {
        userList.clear();
        List<InventorySecurityData> users = fetchUsersFromDB();
        if (users != null) {
            userList.addAll(users);
        }
    }

    /**
     * Fetches the list of users from the database or data source.
     * 
     * @return list of users
     */
    private List<InventorySecurityData> fetchUsersFromDB() {
        // Fetch all users from InventorySecurityManagement class
        return InventorySecurityManagement.getAllUsers();
    }

    /**
     * Deletes a user after confirmation and updates the table view.
     * 
     * @param user the user to delete
     */
    private void deleteUser(InventorySecurityData user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user '" + user.getUserName() + "'?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = securityManager.removeAccount(user.getEmail());
            if (success) {
                userList.remove(user);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User deleted successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete user from database.");
                alert.showAndWait();
            }
        }
    }
}

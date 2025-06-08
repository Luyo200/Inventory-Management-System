package com.yourname.stockwise.security;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class AccountCreateForm {

    private InventorySecurityManagement securityManager;

    public AccountCreateForm() {
        securityManager = new InventorySecurityManagement();
    }

    public void showForm(Stage stage) {
        // UI Components
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(20));
        formPane.setHgap(10);
        formPane.setVgap(10);

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-background-color: white;");
        emailLabel.setTextFill(Color.BLACK);
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        
        PasswordField passwordField = new PasswordField();
        passwordLabel.setStyle("-fx-background-color: white;");
        passwordLabel.setTextFill(Color.BLACK);
        Button createButton = new Button("Create Account");
        createButton.setStyle("-fx-background-color: white;");
        createButton.setTextFill(Color.BLACK);
        Label messageLabel = new Label();
        Button back = new Button("Back");
        Login login = new Login(); // Pass the same manager
        
        back.setOnAction(e-> login.showLoginForm(stage));
        formPane.add(emailLabel, 0, 0);
        formPane.add(emailField, 1, 0);

        formPane.add(passwordLabel, 0, 1);
        formPane.add(passwordField, 1, 1);

        formPane.add(createButton, 1, 2);
        formPane.add(messageLabel, 1, 3);
        formPane.add(back, 1,4);
        formPane.setStyle("-fx-background-color: black;");
        

        createButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in both fields.");
                messageLabel.setStyle("-fx-text-fill: green;");
                return;
            }

            boolean success = securityManager.addAccount(email, password);
            if (success) {
                messageLabel.setText("Account created successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Delay before showing the login form
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(event -> {
                    //Login login = new Login(); // Pass the same manager
                    login.showLoginForm(stage);
                });
                pause.play();

                emailField.clear();
                passwordField.clear();
            } else {
                messageLabel.setText("Account with this email already exists.");
                messageLabel.setStyle("-fx-text-fill: green;");
            }
        });

        Scene scene = new Scene(formPane, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Create Account");
        stage.show();
    }
}

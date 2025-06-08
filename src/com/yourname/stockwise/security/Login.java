package com.yourname.stockwise.security;

import com.yourname.stockwise.app.InventoryApp;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login {

    private AccountCreateForm account = new AccountCreateForm();
    private final InventorySecurityManagement securityManager;

    public Login() {
        securityManager = new InventorySecurityManagement();
    }

    public void showLoginForm(Stage stage) {

        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setHgap(10);
        loginPane.setVgap(10);

        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.WHITE);
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: white; -fx-border-color: skyblue;");
        loginButton.setTextFill(Color.BLACK);

        Button signUpButton = new Button("Don't have an account?");
        signUpButton.setTextFill(Color.BLACK);
        signUpButton.setStyle("-fx-background-color: white; -fx-border-color: skyblue;");

        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setTextFill(Color.BLACK);
        forgotPasswordButton.setStyle("-fx-background-color: white; -fx-border-color: skyblue;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        loginPane.add(emailLabel, 0, 0);
        loginPane.add(emailField, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(passwordField, 1, 1);
        loginPane.add(loginButton, 1, 2);
        loginPane.add(messageLabel, 1, 3);
        loginPane.add(signUpButton, 1, 4);
        loginPane.add(forgotPasswordButton, 4, 1);
        loginPane.setStyle("-fx-background-color: black;");

        // Login button action: Use InventorySecurityManagement methods explicitly
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Email and password cannot be empty.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Use loginWithFeedback method (which uses emailExists and login internally)
            String loginResult = securityManager.loginWithFeedback(email, password);

            if ("success".equals(loginResult)) {
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");
                InventoryApp app = new InventoryApp();
                app.showDashboard(stage);
            } else {
                messageLabel.setText(loginResult);
                messageLabel.setStyle("-fx-text-fill: red;");
                passwordField.clear(); // Clear password on failure
            }
        });

        // Forgot password button: Use emailExists and updatePassword explicitly
        forgotPasswordButton.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Password Recovery");

            GridPane popupPane = new GridPane();
            popupPane.setPadding(new Insets(20));
            popupPane.setHgap(10);
            popupPane.setVgap(10);

            Label emailPrompt = new Label("Enter your registered email:");
            emailPrompt.setTextFill(Color.BLACK);
            TextField emailInput = new TextField();
            Button checkEmailButton = new Button("Submit");
            checkEmailButton.setTextFill(Color.BLACK);
            Label feedbackLabel = new Label();

            Label newPassLabel = new Label("New Password:");
            newPassLabel.setTextFill(Color.BLACK);
            PasswordField newPasswordField = new PasswordField();
            Label confirmPassLabel = new Label("Confirm Password:");
            confirmPassLabel.setTextFill(Color.BLACK);
            PasswordField confirmPasswordField = new PasswordField();
            Button resetButton = new Button("Reset Password");
            resetButton.setTextFill(Color.BLACK);
            Button back = new Button("Back");
            back.setTextFill(Color.BLACK);

            popupPane.add(emailPrompt, 0, 0);
            popupPane.add(emailInput, 0, 1);
            popupPane.add(checkEmailButton, 0, 2);
            popupPane.add(feedbackLabel, 0, 3);
            popupPane.add(back, 0, 4);
            popupPane.setStyle("-fx-background-color: black;");

            // Check email exists
            checkEmailButton.setOnAction(ev -> {
                String enteredEmail = emailInput.getText().trim();
                if (securityManager.emailExists(enteredEmail)) {
                    feedbackLabel.setText("Email confirmed. You can reset your password.");
                    feedbackLabel.setStyle("-fx-text-fill: green;");

                    // Show password reset fields
                    popupPane.add(newPassLabel, 0, 5);
                    popupPane.add(newPasswordField, 0, 6);
                    popupPane.add(confirmPassLabel, 0, 7);
                    popupPane.add(confirmPasswordField, 0, 8);
                    popupPane.add(resetButton, 0, 9);
                } else {
                    feedbackLabel.setText("Email not registered.");
                    feedbackLabel.setStyle("-fx-text-fill: red;");

                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(ev2 -> feedbackLabel.setText(""));
                    pause.play();
                }
            });

            // Reset password
            resetButton.setOnAction(ev -> {
                String newPass = newPasswordField.getText();
                String confirmPass = confirmPasswordField.getText();

                if (!newPass.equals(confirmPass)) {
                    feedbackLabel.setText("Passwords do not match.");
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                } else if (newPass.isEmpty()) {
                    feedbackLabel.setText("Password cannot be empty.");
                    feedbackLabel.setStyle("-fx-text-fill: red;");
                } else {
                    // Update password using InventorySecurityManagement method
                    securityManager.updatePassword(emailInput.getText().trim(), newPass);
                    feedbackLabel.setText("Password reset successful.");
                    feedbackLabel.setStyle("-fx-text-fill: green;");

                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(ev3 -> popup.close());
                    pause.play();
                }
            });

            back.setOnAction(ev -> popup.close());

            Scene popupScene = new Scene(popupPane, 350, 450);
            popup.setScene(popupScene);
            popup.show();
        });

        // Sign up button to open account creation form
        signUpButton.setOnAction(e -> account.showForm(stage));

        Scene scene = new Scene(loginPane, 400, 250);
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    }
}

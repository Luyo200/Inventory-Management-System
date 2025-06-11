package com.yourname.stockwise.security;

import com.yourname.stockwise.app.InventoryApp;
import com.yourname.stockwise.style.HomePage;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Login UI component for the StockWise application.
 * <p>
 * Provides the user interface for logging in with email or username and password,
 * supports password recovery functionality, and navigation back to the home page.
 * </p>
 *
 * <p>This class interacts with {@link InventorySecurityManagement} to handle authentication logic.</p>
 *
 * @author L Mahamba
 * @version 1.0.0
 */
public class Login {

    /** Form to create accounts, currently unused here but instantiated for possible future use */
    private final AccountCreateForm account = new AccountCreateForm();

    /** Security manager instance handling authentication and user management */
    private final InventorySecurityManagement securityManager;

    /**
     * Constructs a new Login instance initializing the security manager.
     */
    public Login() {
        securityManager = new InventorySecurityManagement();
    }

    /**
     * Displays the login form on the provided stage.
     * <p>
     * The form includes inputs for email/username and password, login button,
     * forgot password button, and navigation back to the home page.
     * </p>
     *
     * @param stage the JavaFX stage on which to display the login form
     */
    public void showLoginForm(Stage stage) {
        // Setup GridPane layout for login form
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(30));
        loginPane.setHgap(10);
        loginPane.setVgap(15);
        loginPane.setAlignment(Pos.CENTER);

        // Create labels and input fields
        Label userLabel = new Label("Email or Username:");
        userLabel.setTextFill(Color.WHITE);
        TextField userField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();

        // Label to show messages (errors or success)
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        // Create buttons with styling
        Button loginButton = createStyledButton("Login", "#2196F3");
        Button forgotPasswordButton = createStyledButton("Forgot Password?", "#FF9800");
        Button backButton = createStyledButton("← Back to Home", "#9E9E9E");

        // Add components to the grid layout
        loginPane.add(userLabel, 0, 0);
        loginPane.add(userField, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(passwordField, 1, 1);
        loginPane.add(loginButton, 1, 2);
        loginPane.add(messageLabel, 1, 3);
        loginPane.add(forgotPasswordButton, 1, 5);
        loginPane.add(backButton, 1, 6);

        // Background styling for the pane
        loginPane.setStyle("-fx-background-color: linear-gradient(to right, #0f2027, #203a43, #2c5364);");

        // Login button event handler
        loginButton.setOnAction(e -> {
            String userInput = userField.getText().trim();
            String password = passwordField.getText().trim();

            // Validate inputs are not empty
            if (userInput.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username/Email and password cannot be empty.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Attempt login using security manager
            String loginResult = securityManager.loginWithEmailOrUsername(userInput, password);

            if ("success".equalsIgnoreCase(loginResult)) {
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");
                // Open dashboard on successful login
                new InventoryApp().showDashboard(stage);
            } else {
                // Show error message and clear password field on failure
                messageLabel.setText(loginResult);
                messageLabel.setStyle("-fx-text-fill: red;");
                passwordField.clear();
            }
        });

        // Forgot password button shows the password recovery popup
        forgotPasswordButton.setOnAction(e -> showPasswordRecoveryPopup());

        // Back button navigates back to the home page
        backButton.setOnAction(e -> {
            HomePage home = new HomePage();
            home.showHomePage(stage);
        });

        // Set the scene and show the stage
        Scene scene = new Scene(loginPane, 480, 420);
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    }

    /**
     * Shows a password recovery popup window.
     * <p>
     * Allows the user to enter their registered email, validate it,
     * and reset their password if the email exists.
     * </p>
     */
    private void showPasswordRecoveryPopup() {
        Stage popup = new Stage();
        popup.setTitle("Password Recovery");

        // Setup popup layout
        GridPane popupPane = new GridPane();
        popupPane.setPadding(new Insets(20));
        popupPane.setHgap(10);
        popupPane.setVgap(10);
        popupPane.setAlignment(Pos.CENTER);
        popupPane.setStyle("-fx-background-color: #f4f4f4;");

        Label emailPrompt = new Label("Enter your registered email:");
        TextField emailInput = new TextField();
        Button checkEmailButton = createStyledButton("Submit", "#2196F3");
        Label feedbackLabel = new Label();

        // Components for new password inputs, initially not added
        Label newPassLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        Label confirmPassLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button resetButton = createStyledButton("Reset Password", "#4CAF50");
        Button back = createStyledButton("Back", "#9E9E9E");

        // Add email input and buttons initially
        popupPane.add(emailPrompt, 0, 0);
        popupPane.add(emailInput, 0, 1);
        popupPane.add(checkEmailButton, 0, 2);
        popupPane.add(feedbackLabel, 0, 3);
        popupPane.add(back, 0, 4);

        // Check email button event handler
        checkEmailButton.setOnAction(ev -> {
            String enteredEmail = emailInput.getText().trim();

            if (securityManager.emailExists(enteredEmail)) {
                // Email exists, allow password reset
                feedbackLabel.setText("Email confirmed. You can reset your password.");
                feedbackLabel.setTextFill(Color.GREEN);

                // Add new password fields and reset button
                popupPane.add(newPassLabel, 0, 5);
                popupPane.add(newPasswordField, 0, 6);
                popupPane.add(confirmPassLabel, 0, 7);
                popupPane.add(confirmPasswordField, 0, 8);
                popupPane.add(resetButton, 0, 9);
            } else {
                // Email not registered - show error and clear after 3 seconds
                feedbackLabel.setText("Email not registered.");
                feedbackLabel.setTextFill(Color.RED);

                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(ev2 -> feedbackLabel.setText(""));
                pause.play();
            }
        });

        // Reset password button event handler
        resetButton.setOnAction(ev -> {
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (!newPass.equals(confirmPass)) {
                feedbackLabel.setText("Passwords do not match.");
                feedbackLabel.setTextFill(Color.RED);
            } else if (newPass.isEmpty()) {
                feedbackLabel.setText("Password cannot be empty.");
                feedbackLabel.setTextFill(Color.RED);
            } else {
                // Update password in database and close popup after showing success
                securityManager.updatePassword(emailInput.getText().trim(), newPass);
                feedbackLabel.setText("Password reset successful.");
                feedbackLabel.setTextFill(Color.GREEN);

                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(ev3 -> popup.close());
                pause.play();
            }
        });

        // Back button closes the popup
        back.setOnAction(ev -> popup.close());

        Scene popupScene = new Scene(popupPane, 350, 500);
        popup.setScene(popupScene);
        popup.show();
    }

    /**
     * Helper method to create a styled button with hover effects.
     *
     * @param text  the button text
     * @param color the base background color of the button (hex or named color)
     * @return a styled Button instance
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        // Base style
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;"
        );
        // Mouse hover effect to lighten background
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: derive(" + color + ", 20%);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;"
        ));
        // Mouse exit reverts to original style
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;"
        ));
        return button;
    }
}

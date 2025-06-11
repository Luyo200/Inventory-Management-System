package com.yourname.stockwise.security;

import com.yourname.stockwise.style.HomePage;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX form for creating a new user account in the StockWise application.
 * <p>
 * This class provides fields for user personal information and credentials,
 * enforces password complexity requirements, and restricts account creation to one user.
 * <p>
 * It also features real-time password strength feedback, input validation for email and phone,
 * and navigation back to the home page.
 * <p>
 * On successful account creation, the user is redirected to the login form.
 * 
 * @author L Mahamba
 * @version 1.1.0
 */
public class AccountCreateForm {

    /** Manages security-related operations like account creation and validation */
    private final InventorySecurityManagement securityManager;

    /**
     * Constructs an AccountCreateForm and initializes the security manager.
     */
    public AccountCreateForm() {
        securityManager = new InventorySecurityManagement();
    }

    /**
     * Displays the account creation form on the provided stage.
     *
     * @param stage the primary stage where the form is shown
     */
    public void showForm(Stage stage) {
        // Initialize the main layout GridPane
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(30));
        formPane.setHgap(15);
        formPane.setVgap(15);
        formPane.setAlignment(Pos.CENTER);
        formPane.setStyle("-fx-background-color: linear-gradient(to right, #141e30, #243b55);");

        // Title label at top center spanning two columns
        Label title = new Label("Create Your Account");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.WHITE);
        GridPane.setColumnSpan(title, 2);
        GridPane.setHalignment(title, javafx.geometry.HPos.CENTER);

        // User input fields with prompts
        TextField nameField = new TextField();
        nameField.setPromptText("Enter first name");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Enter surname");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");

        // Labels for the fields
        Label nameLabel = createLabel("First Name:");
        Label surnameLabel = createLabel("Surname:");
        Label phoneLabel = createLabel("Phone:");
        Label usernameLabel = createLabel("Username:");
        Label emailLabel = createLabel("Email:");
        Label passwordLabel = createLabel("Password:");
        Label confirmPasswordLabel = createLabel("Confirm Password:");

        // Password requirements guidance
        Label passwordHint = new Label(
            "Password must be 8–20 characters and include:\n" +
            "• At least one uppercase letter (A–Z)\n" +
            "• At least one lowercase letter (a–z)\n" +
            "• At least one number (0–9)\n" +
            "• At least one special character (!@#$%^&+=)"
        );
        passwordHint.setTextFill(Color.LIGHTGRAY);
        passwordHint.setStyle("-fx-font-size: 12;");

        // Password strength bar and label
        ProgressBar passwordStrengthBar = new ProgressBar(0);
        passwordStrengthBar.setPrefWidth(300);

        Label passwordStrengthLabel = new Label("Password Strength: ");
        passwordStrengthLabel.setTextFill(Color.LIGHTGRAY);
        passwordStrengthLabel.setStyle("-fx-font-size: 12;");

        // Buttons: Create account and Back to home
        Button createButton = new Button("Create Account");
        createButton.setStyle("-fx-background-color: #00c6ff; -fx-text-fill: white; -fx-font-weight: bold;");
        createButton.setPrefWidth(200);

        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-text-fill: white;");
        backButton.setPrefWidth(200);

        // Label to display validation and status messages
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(messageLabel, 2);
        GridPane.setHalignment(messageLabel, javafx.geometry.HPos.CENTER);

        // Add all components to the grid pane
        formPane.add(title, 0, 0);
        formPane.add(nameLabel, 0, 1);             formPane.add(nameField, 1, 1);
        formPane.add(surnameLabel, 0, 2);          formPane.add(surnameField, 1, 2);
        formPane.add(phoneLabel, 0, 3);             formPane.add(phoneField, 1, 3);
        formPane.add(usernameLabel, 0, 4);          formPane.add(usernameField, 1, 4);
        formPane.add(emailLabel, 0, 5);             formPane.add(emailField, 1, 5);
        formPane.add(passwordLabel, 0, 6);          formPane.add(passwordField, 1, 6);
        formPane.add(passwordHint, 1, 7);
        formPane.add(passwordStrengthLabel, 1, 8);
        formPane.add(passwordStrengthBar, 1, 9);
        formPane.add(confirmPasswordLabel, 0, 10);   formPane.add(confirmPasswordField, 1, 10);
        formPane.add(createButton, 1, 11);
        formPane.add(messageLabel, 0, 12);
        formPane.add(backButton, 1, 13);

        // Listener to update password strength as user types
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            double strength = calculatePasswordStrength(newVal);
            passwordStrengthBar.setProgress(strength);

            String strengthText;
            Color color;

            if (strength < 0.3) {
                strengthText = "Very Weak";
                color = Color.RED;
            } else if (strength < 0.5) {
                strengthText = "Weak";
                color = Color.ORANGE;
            } else if (strength < 0.7) {
                strengthText = "Moderate";
                color = Color.GOLD;
            } else if (strength < 0.9) {
                strengthText = "Strong";
                color = Color.LIGHTGREEN;
            } else {
                strengthText = "Very Strong";
                color = Color.GREEN;
            }

            passwordStrengthLabel.setText("Password Strength: " + strengthText);
            passwordStrengthLabel.setTextFill(color);
        });

        // Action for create account button
        createButton.setOnAction(e -> {
            // Prevent more than one account
            if (securityManager.getAccountCount() >= 1) {
                messageLabel.setText("An account already exists. You cannot create another.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Gather and trim input values
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Check for empty fields
            if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() ||
                username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                messageLabel.setText("Please enter a valid email address.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Validate phone format (simple example)
            if (!isValidPhone(phone)) {
                messageLabel.setText("Please enter a valid phone number.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Password validations: length, complexity, match
            if (password.length() < 8 || password.length() > 20) {
                messageLabel.setText("Password must be between 8 and 20 characters.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (!password.matches(".*[A-Z].*")) {
                messageLabel.setText("Password must contain at least one uppercase letter.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (!password.matches(".*[a-z].*")) {
                messageLabel.setText("Password must contain at least one lowercase letter.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (!password.matches(".*\\d.*")) {
                messageLabel.setText("Password must contain at least one number.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (!password.matches(".*[!@#$%^&+=].*")) {
                messageLabel.setText("Password must contain at least one special character (!@#$%^&+=).");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // TODO: Hash password before storing for security
            // String hashedPassword = hashPassword(password);

            // Create new user data object with plain password for now
            InventorySecurityData newUser = new InventorySecurityData(name, surname, phone, username, email, password);

            // Add account using security manager
            boolean success = securityManager.addAccount(newUser);

            if (success) {
                messageLabel.setTextFill(Color.LIGHTGREEN);
                messageLabel.setText("Account created successfully!");

                // Pause briefly before navigating to login screen
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(event -> {
                    Login login = new Login();
                    login.showLoginForm(stage);
                });
                pause.play();

                // Clear input fields and reset password strength
                nameField.clear();
                surnameField.clear();
                phoneField.clear();
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();

                passwordStrengthBar.setProgress(0);
                passwordStrengthLabel.setText("Password Strength: ");
                passwordStrengthLabel.setTextFill(Color.LIGHTGRAY);
            } else {
                messageLabel.setText("Account creation failed.");
                messageLabel.setTextFill(Color.ORANGE);
            }
        });

        // Action to go back to home page
        backButton.setOnAction(e -> {
            HomePage home = new HomePage();
            home.showHomePage(stage);
        });

        // Set scene and show stage
        Scene scene = new Scene(formPane, 600, 650);
        stage.setScene(scene);
        stage.setTitle("Create Account");
        stage.show();
    }

    /**
     * Creates a styled white label.
     * 
     * @param text The label text
     * @return A Label styled with white text color
     */
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        return label;
    }

    /**
     * Calculates a simple password strength score from 0 to 1 based on
     * length and presence of character types.
     * 
     * @param password the password string to evaluate
     * @return a strength score between 0 and 1
     */
    private double calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0;

        double strength = 0;

        // Length points (max 0.3)
        int length = password.length();
        if (length >= 8) {
            strength += Math.min(0.3, 0.3 * ((length - 7) / 13.0)); // max length 20 chars for full score
        }

        // Contains uppercase (0.2)
        if (password.matches(".*[A-Z].*")) strength += 0.2;

        // Contains lowercase (0.2)
        if (password.matches(".*[a-z].*")) strength += 0.2;

        // Contains digit (0.15)
        if (password.matches(".*\\d.*")) strength += 0.15;

        // Contains special char (0.15)
        if (password.matches(".*[!@#$%^&+=].*")) strength += 0.15;

        return Math.min(strength, 1.0);
    }

    /**
     * Validates an email address format with a basic regex pattern.
     * This can be replaced with a more complex or RFC-compliant validation if needed.
     * 
     * @param email the email string to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates a phone number with a basic pattern (digits, spaces, +, -, parentheses).
     * Customize this regex to match expected phone formats.
     * 
     * @param phone the phone string to validate
     * @return true if phone format is valid, false otherwise
     */
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[\\d\\s()+-]{7,15}$";
        return phone.matches(phoneRegex);
    }

    // Placeholder for password hashing (e.g., using BCrypt or similar)
    /*
    private String hashPassword(String password) {
        // Implement password hashing here
        // e.g., return BCrypt.hashpw(password, BCrypt.gensalt());
        return password; // Replace with actual hash
    }
    */
}

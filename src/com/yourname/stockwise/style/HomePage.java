package com.yourname.stockwise.style;

import com.yourname.stockwise.security.AccountCreateForm;
import com.yourname.stockwise.security.Login;
import com.yourname.stockwise.security.UserManagementView;
import com.yourname.stockwise.unlokedDisplay.UnlockedInformation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The home page of the StockWise application.
 * <p>
 * Displays a welcome message, description, and navigation buttons
 * to Sign In, Create Account, and View Inventory sections.
 * </p>
 * Provides stylish buttons with hover effects and gradient background.
 * 
 * @author 
 * @version 1.0.1
 */
public class HomePage {

    /**
     * Shows the main home page scene on the given stage.
     *
     * @param primaryStage the primary stage for this application
     */
    public void showHomePage(Stage primaryStage) {
        // Heading text
        Text heading = new Text("Welcome to StockWise");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        heading.setFill(Color.WHITE);
        heading.setEffect(new DropShadow(5, Color.GRAY));

        // Description text with emoji and line breaks
        Text description = new Text(
            "📦 StockWise is your all-in-one inventory management system.\n" +
            "🛒 Manage products, suppliers, and transactions with ease.\n" +
            "📈 Generate reports and get low-stock alerts.\n\n" +
            "🚀 Track inventory, optimize supply chains, and grow your business."
        );
        description.setFont(Font.font("Segoe UI", 18));
        description.setFill(Color.LIGHTYELLOW);
        description.setWrappingWidth(600);
        description.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Buttons with color and hover styling
        Button signInBtn = createStyledButton("Sign In", "#2196F3", "#FBC02D");
        signInBtn.setOnAction(e -> {
            new Login().showLoginForm(primaryStage);
            System.out.println("Sign In clicked");
        });

        Button createAccountBtn = createStyledButton("Create Account", "#4CAF50", "#FBC02D");
        createAccountBtn.setOnAction(e -> {
            new AccountCreateForm().showForm(primaryStage);
            System.out.println("Create Account clicked");
        });

        // Fixed typo: "View Inventory"
        Button viewInventoryBtn = createStyledButton("View Inventory", "#BBDEFB", "#FBC02D");
        viewInventoryBtn.setOnAction(e -> {
            new UnlockedInformation().showDashboard(primaryStage);
        });

        // Informational message label
        Label messageLabel = new Label("Only Admin User allowed to login/create an account");
        messageLabel.setTextFill(Color.ORANGE);
              // VBox layout containing all UI elements
        VBox layout = new VBox(25); // 25px spacing
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(60));
        layout.getChildren().addAll(heading, description, signInBtn, createAccountBtn, messageLabel, viewInventoryBtn);

        // Gradient background from dark blue to teal
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#1a2980")), new Stop(1, Color.web("#26d0ce")) };
        LinearGradient backgroundGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        layout.setBackground(new Background(new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Scene setup and show stage
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("StockWise Home");
        primaryStage.show();
    }

    /**
     * Creates a styled button with a base color and hover color effect.
     *
     * @param text      the button text
     * @param baseColor the base background color (hex or CSS color string)
     * @param hoverColor the background color when hovered
     * @return a Button instance styled accordingly
     */
    private Button createStyledButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefWidth(220);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: " + baseColor + "; -fx-background-radius: 12;");
        button.setEffect(new DropShadow(4, Color.DARKGRAY));
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-background-radius: 12;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + baseColor + "; -fx-background-radius: 12;"));
        return button;
    }
}

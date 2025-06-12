package com.yourname.stockwise.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.yourname.stockwise.controller.ProductController;
import com.yourname.stockwise.controller.SupplierController;
import com.yourname.stockwise.controller.TransactionController;
import com.yourname.stockwise.dao.ProductDAO;
import com.yourname.stockwise.dao.SupplierDAO;
import com.yourname.stockwise.dao.TransactionDAO;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.security.UserManagementView;
import com.yourname.stockwise.style.HomePage;
import com.yourname.stockwise.util.AlertHelper;
import com.yourname.stockwise.visitor.InventoryReportVisitor;
import com.yourname.stockwise.visitor.LowStockAlertVisitor;
import com.yourname.stockwise.visitor.StockValueCalculatorVisitor;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The {@code InventoryApp} class is a JavaFX application that provides a
 * graphical interface for managing inventory operations such as:
 * <ul>
 * <li>Viewing product listings</li>
 * <li>Adding new products and suppliers</li>
 * <li>Managing stock through transactions</li>
 * <li>Generating low-stock reports</li>
 * <li>Calculating total stock value</li>
 * <li>Viewing supplier and transaction records</li>
 * </ul>
 *
 * <p>
 * The application uses a simple visitor pattern to support analytics features,
 * such as calculating stock value and identifying low stock products. Data is
 * fetched and managed through static utility classes such as
 * {@code InventoryData}, {@code SupplierData}, and {@code TransactionData}.
 * </p>
 *
 * <p>
 * The GUI is built using JavaFX, with each section (dashboard, forms, tables)
 * presented as different views navigated from a central dashboard.
 * </p>
 *
 * <p>
 * <b>Note:</b> This application assumes all data is managed in-memory or
 * through simple storage as defined in the supporting data classes.
 * </p>
 *
 * @author Luyolo
 * @version Inventory
 */

public class InventoryApp extends Application {

	/**
	 * Launches the JavaFX application.
	 *
	 * @param args the command-line arguments (not used)
	 */

	
	private HomePage home = new HomePage();
	private UserManagementView n = new UserManagementView();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
		

		
	}

	/**
	 * Initializes and starts the JavaFX application.
	 *
	 * @param primaryStage the primary stage for this application
	 * @throws Exception if any exception occurs during application start
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {

		//showDashboard(primaryStage);
		HomePage home = new HomePage();
		home.showHomePage(primaryStage); // Set homepage on launch

	}

	/**
	 * Displays a table view of all products in inventory.
	 *
	 * @param stage the JavaFX stage to display the product table
	 */

	public void showProductTable(Stage stage) {

	    ProductDAO dao = new ProductDAO();
	    List<Product> allProducts = dao.getAllProducts();
	    ObservableList<Product> productList = FXCollections.observableArrayList(allProducts);

	    TableView<Product> tableView = new TableView<>(productList);
	    tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	    tableView.setPlaceholder(new Label("No products available"));

	    // Define columns
	    TableColumn<Product, String> idCol = new TableColumn<>("ID");
	    idCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId()));

	    TableColumn<Product, String> nameCol = new TableColumn<>("Name");
	    nameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

	    TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
	    qtyCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));

	    TableColumn<Product, Integer> thresholdCol = new TableColumn<>("Threshold");
	    thresholdCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getThreshold()));

	    TableColumn<Product, Double> priceCol = new TableColumn<>("Unit Price");
	    priceCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUnitPrice()));
	    priceCol.setCellFactory(col -> new TableCell<>() {
	        @Override
	        protected void updateItem(Double price, boolean empty) {
	            super.updateItem(price, empty);
	            if (empty || price == null) {
	                setText(null);
	            } else {
	                setText(String.format("$%.2f", price));
	            }
	        }
	    });

	    TableColumn<Product, String> dateAddedCol = new TableColumn<>("Date Added");
	    dateAddedCol.setCellValueFactory(cellData -> {
	        LocalDateTime dateAdded = cellData.getValue().getDateAdded();
	        String formattedDate = dateAdded != null ? dateAdded.toLocalDate().toString() : "N/A";
	        return new ReadOnlyStringWrapper(formattedDate);
	    });

	    tableView.getColumns().addAll(idCol, nameCol, qtyCol, thresholdCol, priceCol, dateAddedCol);

	    tableView.setStyle("""
	        -fx-font-size: 14px;
	        -fx-border-color: #007acc;
	        -fx-border-width: 2;
	        -fx-border-radius: 10;
	        -fx-background-radius: 10;
	    """);

	    // Wrap TableView inside a ScrollPane with fixed max height to enable scrolling
	    ScrollPane scrollPane = new ScrollPane(tableView);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setFitToHeight(true);
	    scrollPane.setPrefViewportHeight(400);  // Limit viewport height to trigger scroll
	    scrollPane.setMaxHeight(400);
	    scrollPane.setStyle("-fx-background: transparent;");

	    // Buttons
	    Button backBtn = createStyledButton("⬅ Back", e -> showDashboard(stage));
	    Button addProductBtn = createStyledButton("➕ Add Product", e -> {
	        ProductController pc = new ProductController();
	        pc.showAddProductForm(stage);
	    });
	    Button deleteBtn = createStyledButton("🗑 Delete", e -> {
	        Product selected = tableView.getSelectionModel().getSelectedItem();
	        if (selected != null) {
	            dao.deleteProduct(selected.getId());
	            productList.remove(selected);
	        }
	    });

	    HBox buttonBar = new HBox(20, backBtn, addProductBtn, deleteBtn);
	    buttonBar.setAlignment(Pos.CENTER);
	    buttonBar.setPadding(new Insets(10, 0, 0, 0));

	    // Date filter
	    DatePicker datePicker = new DatePicker();
	    datePicker.setPromptText("Filter by Date");

	    Button searchBtn = createStyledButton("🔍 Search", e -> {
	        LocalDate selectedDate = datePicker.getValue();
	        if (selectedDate != null) {
	            List<Product> filtered = dao.getProductsByDate(selectedDate);
	            productList.setAll(filtered);
	        }
	    });

	    HBox searchBar = new HBox(10, datePicker, searchBtn);
	    searchBar.setAlignment(Pos.CENTER);

	    // Layout
	    VBox root = new VBox(15, searchBar, scrollPane, buttonBar);
	    root.setPadding(new Insets(25));
	    root.setAlignment(Pos.CENTER);
	    root.setStyle("""
	        -fx-background-color: linear-gradient(to bottom, #e0eafc, #cfdef3);
	        -fx-border-radius: 15;
	        -fx-background-radius: 15;
	    """);

	    Scene scene = new Scene(root, 800, 580);
	    stage.setScene(scene);
	    stage.setTitle("Inventory Products");
	    stage.show();
	}


	/**
	 * Displays the main dashboard with navigation buttons to various application
	 * features.
	 *
	 * @param stage the JavaFX stage to display the dashboard
	 */
	public void showDashboard(Stage stage) {
		// Description text
		Text descriptionText = new Text("Manage your inventory efficiently with quick access to products, reports, suppliers, and transactions. Navigate using the options below.");
		descriptionText.setWrappingWidth(600);
		descriptionText.setTextAlignment(TextAlignment.CENTER);
		descriptionText.setStyle("""
		        -fx-font-size: 14px;
		        -fx-fill: #333333;
		        -fx-font-family: 'Segoe UI', sans-serif;
		    """);
		// Styled buttons
		Button viewProductsBtn = createStyledButton("📦 View Products", e -> showProductTable(stage));
		Button lowStockReportBtn = createStyledButton("⚠️ Low Stock Report", e -> showLowStockReport(stage));
		Button calcStockValueBtn = createStyledButton("💰 Calculate Stock Value", e -> showStockValue(stage));
		Button showTransactionsBtn = createStyledButton("📄 Show Transactions", e -> showTransactions(stage));
		Button viewSuppliersBtn = createStyledButton("🏭 View Suppliers", e -> showSuppliers(stage));
		Button viewReportBtn = createStyledButton("📊 View Inventory Report", e -> showInventoryReport(stage));
		Button backBtn = createStyledButton("🔙 Logout", e -> home.showHomePage(stage));
		Button b = createStyledButton("View User Details", e-> n.show(stage));
       
		// FlowPane for flexible layout with wrapping
		FlowPane flowPane = new FlowPane();
		flowPane.setHgap(20);
		flowPane.setVgap(20);
		
		flowPane.setPadding(new Insets(25));
		flowPane.setAlignment(Pos.CENTER);
		flowPane.setPrefWrapLength(600);  // Width at which buttons wrap to next line

		// Add buttons to FlowPane
		flowPane.getChildren().addAll(
		    viewProductsBtn,
		    lowStockReportBtn,
		    calcStockValueBtn,
		    viewSuppliersBtn,
		    showTransactionsBtn,
		    viewReportBtn,
		    b
		);

		// Stylish VBox container (card look)
		VBox optionCard = new VBox(25);
		optionCard.setAlignment(Pos.CENTER);
		optionCard.setPadding(new Insets(30));
		optionCard.setStyle("""
		        -fx-background-color: white;
		        -fx-border-color: #007acc;
		        -fx-border-width: 2;
		        -fx-border-radius: 20;
		        -fx-background-radius: 20;
		        -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.2), 10, 0, 5, 5);
		    """);
         b.setStyle("""
 		        -fx-background-color: white;
 		        -fx-border-color: #007acc;
 		        -fx-border-width: 2;
 		        -fx-border-radius: 20;
 		        -fx-background-radius: 20;
 		        -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.2), 10, 0, 5, 5);
 		    """);
		Label dashboardLabel = new Label("📁 Dashboard Options");
		dashboardLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007acc;");
		optionCard.getChildren().addAll(dashboardLabel, flowPane);

		// Header
		Label header = new Label("📋 Inventory Management Dashboard");
		header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #003366;");

		// Back button pane aligned to bottom center
		HBox backPane = new HBox(backBtn);
		backPane.setAlignment(Pos.CENTER);
		backPane.setPadding(new Insets(20, 0, 0, 0));

		// Main layout
		VBox root = new VBox(40,descriptionText, header, optionCard, backPane );
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(40));
		root.setStyle("""
		        -fx-background-color: linear-gradient(to bottom, #e0eafc, #cfdef3);
		    """);

		// ScrollPane in case of smaller screens
		ScrollPane scrollPane = new ScrollPane(root);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		// Final scene setup
		Scene scene = new Scene(scrollPane, 700, 650);
		scene.setFill(Color.LIGHTBLUE);

		stage.setScene(scene);
		stage.setTitle("INVENTORY MANAGEMENT SYSTEM");
		stage.show();

	}

	// Helper method to create consistent styled buttons (outside showDashboard
	// method)
	private Button createStyledButton(String text, EventHandler<ActionEvent> handler) {
		Button btn = new Button(text);
		btn.setPrefWidth(230);
		btn.setPrefHeight(45);
		btn.setStyle("""
				    -fx-background-color: #ffffff;
				    -fx-border-color: #007acc;
				    -fx-border-width: 2;
				    -fx-border-radius: 10;
				    -fx-background-radius: 10;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				    -fx-text-fill: #007acc;
				""");

		btn.setOnMouseEntered(e -> btn.setStyle("""
				    -fx-background-color: #007acc;
				    -fx-text-fill: white;
				    -fx-border-radius: 10;
				    -fx-background-radius: 10;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				"""));

		btn.setOnMouseExited(e -> btn.setStyle("""
				    -fx-background-color: #ffffff;
				    -fx-border-color: #007acc;
				    -fx-border-width: 2;
				    -fx-border-radius: 10;
				    -fx-background-radius: 10;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				    -fx-text-fill: #007acc;
				"""));

		btn.setOnAction(handler);
		return btn;
	}

	/**
	 * Displays a comprehensive inventory report on the given stage.
	 * <p>
	 * This method uses the {@link InventoryReportVisitor} to visit all products,
	 * suppliers, and transactions in the inventory system, collecting their string
	 * summaries. It then formats these summaries into sections and displays them in
	 * a scrollable label. A "Back" button is provided to return to the dashboard
	 * view.
	 * </p>
	 *
	 * @param stage the JavaFX {@link Stage} where the report will be shown
	 */
	private void showInventoryReport(Stage stage) {
		InventoryReportVisitor reportVisitor = new InventoryReportVisitor();

		// Fetch data
		ProductDAO productDAO = new ProductDAO();
		SupplierDAO supplierDAO = new SupplierDAO();
		TransactionDAO transactionDAO = new TransactionDAO();

		productDAO.getAllProducts().forEach(p -> p.accept(reportVisitor));
		supplierDAO.getAllSuppliers().forEach(s -> s.accept(reportVisitor));
		transactionDAO.getAllTransactions().forEach(t -> t.accept(reportVisitor));

		// Create card containers for each summary type
		VBox productCards = createReportCards(reportVisitor.getProductSummaries(), "#3498db");
		VBox supplierCards = createReportCards(reportVisitor.getSupplierSummaries(), "#27ae60");
		VBox transactionCards = createReportCards(reportVisitor.getTransactionSummaries(), "#e67e22");

		// ScrollPanes for scrolling
		ScrollPane productScroll = new ScrollPane(productCards);
		styleScrollPane(productScroll);

		ScrollPane supplierScroll = new ScrollPane(supplierCards);
		styleScrollPane(supplierScroll);

		ScrollPane transactionScroll = new ScrollPane(transactionCards);
		styleScrollPane(transactionScroll);

		// TabPane for sections
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(new Tab("Products", productScroll), new Tab("Suppliers", supplierScroll),
				new Tab("Transactions", transactionScroll));
		tabPane.getTabs().forEach(tab -> tab.setClosable(false));
		tabPane.setTabMinWidth(160);
		tabPane.setTabMaxWidth(220);
		tabPane.setStyle("""
				    -fx-background-color: transparent;
				    -fx-padding: 8 0 0 0;
				""");

		// Create a header label
		Label header = new Label("Inventory Report");
		header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
		header.setTextFill(Color.web("#2c3e50"));
		header.setAlignment(Pos.CENTER);
		header.setMaxWidth(Double.MAX_VALUE);

		// Styled Back button
		Button backBtn = new Button("⬅ Back");
		styleBackButton(backBtn);
		backBtn.setOnAction(e -> showDashboard(stage));

		// Main container with elegant background
		VBox mainContainer = new VBox(20, header, tabPane, backBtn);
		mainContainer.setPadding(new Insets(30));
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setStyle("""
				    -fx-background-color: linear-gradient(to bottom right, #f0f4f8, #d9e2ec);
				    -fx-background-radius: 20;
				    -fx-border-radius: 20;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 5);
				""");

		// Scene setup
		Scene scene = new Scene(mainContainer, 900, 700);
		stage.setScene(scene);
		stage.setTitle("Inventory Report");
		stage.show();
	}

	// Create card-style report items with colored accent border
	private VBox createReportCards(List<String> summaries, String accentColor) {
		VBox container = new VBox(15);
		container.setPadding(new Insets(15));
		container.setFillWidth(true);

		if (summaries.isEmpty()) {
			Label noData = new Label("No data available.");
			noData.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 16));
			noData.setTextFill(Color.web("#7f8c8d"));
			noData.setAlignment(Pos.CENTER);
			noData.setMaxWidth(Double.MAX_VALUE);
			container.getChildren().add(noData);
			return container;
		}

		for (String summary : summaries) {
			Label summaryLabel = new Label(summary);
			summaryLabel.setWrapText(true);
			summaryLabel.setFont(Font.font("Segoe UI", 16));
			summaryLabel.setTextFill(Color.web("#34495e"));
			summaryLabel.setPadding(new Insets(12, 15, 12, 15));
			summaryLabel.setStyle(String.format("""
					    -fx-background-color: white;
					    -fx-background-radius: 12;
					    -fx-border-radius: 12;
					    -fx-border-color: %s;
					    -fx-border-width: 3;
					    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 3);
					""", accentColor));

			container.getChildren().add(summaryLabel);
		}

		return container;
	}

	// Style ScrollPane to blend nicely
	private void styleScrollPane(ScrollPane scrollPane) {
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("""
				    -fx-background-color: transparent;
				    -fx-padding: 0 5 0 0;
				""");
	}

	// Style back button with hover effect
	private void styleBackButton(Button button) {
		button.setStyle("""
				    -fx-background-color: #2980b9;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-font-size: 15px;
				    -fx-background-radius: 12;
				    -fx-padding: 10 25 10 25;
				    -fx-cursor: hand;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
				""");
		button.setOnMouseEntered(e -> button.setStyle("""
				    -fx-background-color: #1f4f73;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-font-size: 15px;
				    -fx-background-radius: 12;
				    -fx-padding: 10 25 10 25;
				    -fx-cursor: hand;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);
				"""));
		button.setOnMouseExited(e -> button.setStyle("""
				    -fx-background-color: #2980b9;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-font-size: 15px;
				    -fx-background-radius: 12;
				    -fx-padding: 10 25 10 25;
				    -fx-cursor: hand;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
				"""));
	}

	// Helper method to create card-style summaries from a list of summary strings
	private VBox createSummaryCards(List<String> summaries) {
		VBox container = new VBox(12);
		container.setPadding(new Insets(10));
		container.setFillWidth(true);

		if (summaries.isEmpty()) {
			Label noData = new Label("No data available.");
			noData.setFont(Font.font("Arial", FontPosture.ITALIC, 16));
			noData.setTextFill(Color.web("#7f8c8d"));
			noData.setAlignment(Pos.CENTER);
			noData.setMaxWidth(Double.MAX_VALUE);
			container.getChildren().add(noData);
			return container;
		}

		for (String summary : summaries) {
			Label summaryLabel = new Label(summary);
			summaryLabel.setWrapText(true);
			summaryLabel.setFont(Font.font("Arial", 16));
			summaryLabel.setTextFill(Color.web("#2c3e50"));
			summaryLabel.setPadding(new Insets(10));
			summaryLabel.setStyle("""
					    -fx-background-color: white;
					    -fx-background-radius: 10;
					    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);
					""");

			container.getChildren().add(summaryLabel);
		}

		return container;
	}

	private TableView<String> createTable(java.util.List<String> data, String columnName) {
		TableView<String> table = new TableView<>();

		TableColumn<String, String> column = new TableColumn<>(columnName);
		column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
		column.setMinWidth(700);

		table.getColumns().add(column);
		table.getItems().addAll(data);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		return table;
	}

	/**
	 * Displays a report of products that are below their stock threshold on the
	 * specified stage.
	 * <p>
	 * This method uses the {@link LowStockAlertVisitor} to inspect all products in
	 * the inventory and collects those with quantities below their defined
	 * threshold. It then presents a message listing all low stock products with
	 * their quantities. If no products are low in stock, a corresponding message is
	 * displayed instead. A "Back" button allows the user to return to the dashboard
	 * view.
	 * </p>
	 * 
	 * @param stage the JavaFX {@link Stage} on which to display the low stock
	 *              report
	 */

	public void showLowStockReport(Stage stage) {
	    // Load all products from DB
	    ProductDAO productDAO = new ProductDAO();
	    List<Product> allProducts = productDAO.getAllProducts();

	    // Use visitor to find products below threshold
	    LowStockAlertVisitor visitor = new LowStockAlertVisitor();
	    allProducts.forEach(p -> p.accept(visitor));
	    List<Product> lowStockList = visitor.getLowStockProducts();

	    ObservableList<Product> lowStockProducts = FXCollections.observableArrayList(lowStockList);

	    // ===== Header =====
	    Label header = new Label("📦 Low Stock Report");
	    header.setFont(Font.font("Arial", FontWeight.BOLD, 28));
	    header.setTextFill(Color.web("#2c3e50"));
	    header.setAlignment(Pos.CENTER);
	    header.setMaxWidth(Double.MAX_VALUE);

	    // ===== TableView =====
	    TableView<Product> tableView = new TableView<>(lowStockProducts);
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    tableView.setPlaceholder(new Label("✔ All products are above threshold."));

	    TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
	    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

	    TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
	    qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

	    TableColumn<Product, Integer> thresholdCol = new TableColumn<>("Threshold");
	    thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));

	    nameCol.setStyle("-fx-alignment: CENTER-LEFT;");
	    qtyCol.setStyle("-fx-alignment: CENTER;");
	    thresholdCol.setStyle("-fx-alignment: CENTER;");

	    tableView.getColumns().addAll(nameCol, qtyCol, thresholdCol);
	    tableView.setStyle("-fx-font-size: 14px;");

	    // ===== Back Button =====
	    Button backBtn = new Button("⬅ Back");
	    backBtn.setStyle("-fx-background-color: #3498db; "
	            + "-fx-text-fill: white; "
	            + "-fx-font-weight: bold; "
	            + "-fx-background-radius: 8; "
	            + "-fx-padding: 8 16 8 16; "
	            + "-fx-cursor: hand;");
	    backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2980b9; "
	            + "-fx-text-fill: white; "
	            + "-fx-font-weight: bold; "
	            + "-fx-background-radius: 8; "
	            + "-fx-padding: 8 16 8 16; "
	            + "-fx-cursor: hand;"));
	    backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #3498db; "
	            + "-fx-text-fill: white; "
	            + "-fx-font-weight: bold; "
	            + "-fx-background-radius: 8; "
	            + "-fx-padding: 8 16 8 16; "
	            + "-fx-cursor: hand;"));
	    backBtn.setOnAction(e -> showDashboard(stage));

	    HBox buttonBox = new HBox(backBtn);
	    buttonBox.setAlignment(Pos.CENTER_RIGHT);
	    buttonBox.setPadding(new Insets(10, 0, 0, 0));

	    // ===== Center Panel =====
	    VBox centerBox = new VBox(15, tableView, buttonBox);
	    centerBox.setPadding(new Insets(20));
	    centerBox.setAlignment(Pos.CENTER);

	    // ===== Root Layout =====
	    BorderPane root = new BorderPane();
	    root.setTop(header);
	    BorderPane.setAlignment(header, Pos.CENTER);
	    root.setCenter(centerBox);
	    root.setPadding(new Insets(30));
	    root.setStyle("-fx-background-color: linear-gradient(to bottom right, #eaf6ff, #ffffff); "
	            + "-fx-background-radius: 15;");

	    Scene scene = new Scene(root, 700, 500);
	    stage.setScene(scene);
	    stage.setTitle("Low Stock Report");
	    stage.show();
	}




	/**
	 * Displays the total inventory value on the given JavaFX stage.
	 * <p>
	 * This method calculates the total value of all products in the inventory by
	 * applying the {@link StockValueCalculatorVisitor} to each product. It then
	 * updates the stage to show the total value with a label and provides a "Back"
	 * button to return to the dashboard.
	 * </p>
	 * 
	 * @param stage the JavaFX {@link Stage} on which to display the inventory value
	 */

	private void showStockValue(Stage stage) {
		ProductDAO productDAO = new ProductDAO();
		List<Product> allProducts = productDAO.getAllProducts();
		StockValueCalculatorVisitor visitor = new StockValueCalculatorVisitor();

		for (Product product : allProducts) {
			product.accept(visitor);
		}

		Label label = new Label(String.format("Total Inventory Value: R%.2f", visitor.getTotalValue()));
		Button backBtn = new Button("Back");
		backBtn.setOnAction(e -> showDashboard(stage));

		VBox root = new VBox(15, label, backBtn);
		root.setStyle("-fx-padding: 20; -fx-alignment: center;");

		stage.setScene(new Scene(root, 400, 200));
	}

	/**
	 * Displays a table view of all registered suppliers.
	 *
	 * @param stage the JavaFX stage to display the suppliers
	 */

	public void showSuppliers(Stage stage) {
	    // Create DAO and fetch data
	    SupplierDAO supplierDAO = new SupplierDAO();
	    List<Supplier> supplierListFromDB = supplierDAO.getAllSuppliers();
	    ObservableList<Supplier> supplierList = FXCollections.observableArrayList(supplierListFromDB);

	    TableView<Supplier> tableView = new TableView<>(supplierList);

	    TableColumn<Supplier, String> idCol = new TableColumn<>("ID");
	    idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));

	    TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
	    nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));

	    TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
	    emailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));

	    TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
	    phoneCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhone()));

	    TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
	    addressCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAddress()));

	    // ➕ NEW: Created At Date Column
	    TableColumn<Supplier, String> dateCol = new TableColumn<>("Date Added");
	    dateCol.setCellValueFactory(data -> {
	        LocalDateTime dateTime = data.getValue().getDateAdded();
	        String formattedDate = dateTime != null
	                ? dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
	                : "N/A";
	        return new ReadOnlyStringWrapper(formattedDate);
	    });

	    // Delete button column
	    TableColumn<Supplier, Void> deleteCol = new TableColumn<>("Delete");
	    deleteCol.setMaxWidth(100);
	    deleteCol.setStyle("-fx-alignment: CENTER;");
	    deleteCol.setCellFactory(col -> new TableCell<>() {
	        private final Button deleteButton = new Button("Delete");

	        {
	            deleteButton.setStyle(
	                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
	                "-fx-cursor: hand; -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
	            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
	                "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; " +
	                "-fx-cursor: hand; -fx-padding: 5 10 5 10; -fx-background-radius: 5;"));
	            deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
	                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
	                "-fx-cursor: hand; -fx-padding: 5 10 5 10; -fx-background-radius: 5;"));

	            deleteButton.setOnAction(e -> {
	                Supplier supplier = getTableView().getItems().get(getIndex());
	                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	                alert.setTitle("Confirm Delete");
	                alert.setHeaderText(null);
	                alert.setContentText("Are you sure you want to delete supplier: " + supplier.getName() + "?");
	                alert.showAndWait().ifPresent(response -> {
	                    if (response == ButtonType.OK) {
	                        boolean success = supplierDAO.deleteSupplier(supplier.getId());
	                        if (success) {
	                            getTableView().getItems().remove(supplier);
	                        } else {
	                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
	                            errorAlert.setTitle("Delete Failed");
	                            errorAlert.setHeaderText(null);
	                            errorAlert.setContentText("Failed to delete supplier. Try again.");
	                            errorAlert.showAndWait();
	                        }
	                    }
	                });
	            });
	        }

	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            setGraphic(empty ? null : deleteButton);
	        }
	    });

	    // Add all columns
	    tableView.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, addressCol, dateCol, deleteCol);
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    Button backBtn = new Button("Back");
	    backBtn.setOnAction(e -> showDashboard(stage));

	    Button addSupplierBtn = new Button("Add Supplier");
	    SupplierController supplierController = new SupplierController();
	    addSupplierBtn.setOnAction(e -> supplierController.showAddSupplierForm(stage));

	    Button refreshBtn = new Button("Refresh");
	    refreshBtn.setOnAction(e -> {
	        List<Supplier> refreshedList = supplierDAO.getAllSuppliers();
	        tableView.setItems(FXCollections.observableArrayList(refreshedList));
	    });

	    HBox buttons = new HBox(10, backBtn, addSupplierBtn, refreshBtn);
	    buttons.setPadding(new Insets(10));
	    buttons.setAlignment(Pos.CENTER_RIGHT);

	    VBox root = new VBox(10, tableView, buttons);
	    root.setPadding(new Insets(20));
	    root.setStyle("-fx-background-color: #f0f4f7; -fx-border-radius: 10; -fx-background-radius: 10;");

	    stage.setScene(new Scene(root, 900, 500));
	    stage.setTitle("Supplier List");
	    stage.show();
	}

	/**
	 * Displays a table view of all recorded transactions.
	 *
	 * @param stage the JavaFX stage to display the transactions
	 */

	public void showTransactions(Stage stage) {
	    TransactionDAO transactionDAO = new TransactionDAO();
	    List<Transaction> transactionListFromDB = transactionDAO.getAllTransactions();
	    ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactionListFromDB);

	    TableView<Transaction> tableView = new TableView<>(transactionList);
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    TableColumn<Transaction, String> idCol = new TableColumn<>("ID");
	    idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
	    idCol.setMaxWidth(80);
	    idCol.setStyle("-fx-alignment: CENTER;");

	    TableColumn<Transaction, String> productCol = new TableColumn<>("Product");
	    productCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getProduct().getName()));

	    TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
	    typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().toString()));
	    typeCol.setMaxWidth(100);
	    typeCol.setStyle("-fx-alignment: CENTER;");

	    TableColumn<Transaction, Integer> qtyCol = new TableColumn<>("Quantity");
	    qtyCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
	    qtyCol.setMaxWidth(100);
	    qtyCol.setStyle("-fx-alignment: CENTER;");

	    TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
	    dateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTimestamp().toString()));

	    TableColumn<Transaction, Void> deleteCol = new TableColumn<>("Delete");
	    deleteCol.setMaxWidth(100);
	    deleteCol.setStyle("-fx-alignment: CENTER;");

	    deleteCol.setCellFactory(col -> new TableCell<>() {
	        private final Button deleteButton = new Button("Delete");

	        {
	            deleteButton.setStyle(
	                    "-fx-background-color: #e74c3c; " +
	                    "-fx-text-fill: white; " +
	                    "-fx-font-weight: bold; " +
	                    "-fx-cursor: hand; " +
	                    "-fx-padding: 5 10 5 10; " +
	                    "-fx-background-radius: 5;");

	            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
	                    "-fx-background-color: #c0392b; " +
	                    "-fx-text-fill: white; " +
	                    "-fx-font-weight: bold; " +
	                    "-fx-cursor: hand; " +
	                    "-fx-padding: 5 10 5 10; " +
	                    "-fx-background-radius: 5;"));

	            deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
	                    "-fx-background-color: #e74c3c; " +
	                    "-fx-text-fill: white; " +
	                    "-fx-font-weight: bold; " +
	                    "-fx-cursor: hand; " +
	                    "-fx-padding: 5 10 5 10; " +
	                    "-fx-background-radius: 5;"));

	            deleteButton.setOnAction(e -> {
	                Transaction transaction = getTableView().getItems().get(getIndex());
	                boolean confirmed = AlertHelper.showConfirmation("Confirm Delete",
	                        "Are you sure you want to delete transaction " + transaction.getId() + "?");
	                if (confirmed) {
	                    transactionDAO.deleteTransaction(transaction.getId());
	                    transactionList.remove(transaction);
	                }
	            });
	        }

	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty) {
	                setGraphic(null);
	            } else {
	                setGraphic(deleteButton);
	            }
	        }
	    });

	    tableView.getColumns().addAll(idCol, productCol, typeCol, qtyCol, dateCol, deleteCol);

	    Button backBtn = new Button("Back");
	    backBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
	    backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;"));
	    backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;"));

	    backBtn.setOnAction(e -> showDashboard(stage));

	    Button addTransaction = new Button("Add Transaction");
	    addTransaction.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
	    addTransaction.setOnMouseEntered(e -> addTransaction.setStyle("-fx-background-color: #1e8449; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;"));
	    addTransaction.setOnMouseExited(e -> addTransaction.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20 8 20; -fx-background-radius: 5; -fx-cursor: hand;"));

	    TransactionController c = new TransactionController();
	    addTransaction.setOnAction(e -> c.showAddTransactionForm(stage));

	    HBox buttonBox = new HBox(10, backBtn, addTransaction);
	    buttonBox.setPadding(new Insets(10));
	    buttonBox.setAlignment(Pos.CENTER_RIGHT);

	    // Wrap TableView inside a ScrollPane
	    ScrollPane scrollPane = new ScrollPane(tableView);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setFitToHeight(true);

	    VBox root = new VBox(10, scrollPane, buttonBox);
	    root.setPadding(new Insets(20));
	    root.setStyle("-fx-background-color: #f0f4f7; -fx-border-radius: 10; -fx-background-radius: 10;");

	    stage.setScene(new Scene(root, 750, 450));
	    stage.setTitle("Transactions");
	    stage.show();
	}

	/**
	 * Clears the text fields provided.
	 *
	 * @param fields one or more {@link TextField} instances to clear
	 */

	public void clearFields(TextField... fields) {
		for (TextField field : fields) {
			field.clear();
		}
	}

}

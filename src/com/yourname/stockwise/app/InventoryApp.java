package com.yourname.stockwise.app;

import java.util.List;

import com.yourname.stockwise.controller.ProductController;
import com.yourname.stockwise.controller.SupplierController;
import com.yourname.stockwise.controller.TransactionController;
import com.yourname.stockwise.dao.ProductDAO;
import com.yourname.stockwise.dao.SupplierDAO;
import com.yourname.stockwise.dao.TransactionDAO;
import com.yourname.stockwise.data.InventoryData;
import com.yourname.stockwise.data.SupplierData;
import com.yourname.stockwise.data.TransactionData;
import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Supplier;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.security.Login;
import com.yourname.stockwise.visitor.InventoryReportVisitor;
import com.yourname.stockwise.visitor.LowStockAlertVisitor;
import com.yourname.stockwise.visitor.StockValueCalculatorVisitor;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	private TransactionController c;
	private ProductController Pc;
	private SupplierController Sc;
	private ProductDAO dao= new ProductDAO();
	
	private Login l = new Login();
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
		InventoryReportVisitor reportVisitor = new InventoryReportVisitor();

		for (Product p : InventoryData.getAllProducts()) {
			p.accept(reportVisitor);
		}
		for (Supplier s : SupplierData.getAllSuppliers()) {
			s.accept(reportVisitor);
		}
		for (Transaction t : TransactionData.getAllTransactions()) {
			t.accept(reportVisitor);
		}

		System.out.println("Products:");
		reportVisitor.getProductSummaries().forEach(System.out::println);

		System.out.println("Suppliers:");
		reportVisitor.getSupplierSummaries().forEach(System.out::println);

		System.out.println("Transactions:");
		reportVisitor.getTransactionSummaries().forEach(System.out::println);

	}

	/**
	 * Initializes and starts the JavaFX application.
	 *
	 * @param primaryStage the primary stage for this application
	 * @throws Exception if any exception occurs during application start
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Login login = new Login();
		login.showLoginForm(primaryStage);
		//showDashboard(primaryStage);
		 
		

	}

	/**
	 * Displays a table view of all products in inventory.
	 *
	 * @param stage the JavaFX stage to display the product table
	 */

	public void showProductTable(Stage stage) {
	    // Create DAO instance and fetch products
	    ProductDAO dao = new ProductDAO();
	    List<Product> allProducts = dao.getAllProducts(); // Load from DB

	    // Convert to ObservableList for TableView
	    ObservableList<Product> productList = FXCollections.observableArrayList(allProducts);

	    // Create TableView and columns
	    TableView<Product> tableView = new TableView<>(productList);

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

	    // Add columns to table
	    tableView.getColumns().addAll(idCol, nameCol, qtyCol, thresholdCol, priceCol);

	    // Buttons
	    Button backBtn = new Button("Back");
	    backBtn.setOnAction(e -> showDashboard(stage));

	    Button addProductBtn = new Button("Add Product");
	    ProductController pc = new ProductController();  // ensure this is declared
	    addProductBtn.setOnAction(e -> pc.showAddProductForm(stage));

	    HBox buttonBar = new HBox(10, backBtn, addProductBtn);
	    buttonBar.setAlignment(Pos.CENTER);

	    // Layout
	    VBox root = new VBox(10, tableView, buttonBar);
	    root.setStyle("-fx-padding: 20");

	    // Show scene
	    Scene scene = new Scene(root, 700, 500);
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
		// Dashboard action buttons
		Button viewProductsBtn = new Button("View Products");
		viewProductsBtn.setOnAction(e -> showProductTable(stage));

		Button lowStockReportBtn = new Button("Low Stock Report");
		lowStockReportBtn.setOnAction(e -> showLowStockReport(stage));

		Button calcStockValueBtn = new Button("Calculate Stock Value");
		calcStockValueBtn.setOnAction(e -> showStockValue(stage));

		Button showTransactions = new Button("Show Transactions");
		showTransactions.setOnAction(e -> showTransactions(stage));

		Button viewSuppliers = new Button("View Suppliers");
		viewSuppliers.setOnAction(e -> showSuppliers(stage));

		Button viewReportBtn = new Button("View Inventory Report");
		viewReportBtn.setOnAction(e -> showInventoryReport(stage));

		// Optional: Set preferred button width for uniformity
		Button[] buttons = { viewProductsBtn, lowStockReportBtn, calcStockValueBtn, showTransactions, viewSuppliers,
				viewReportBtn };
		for (Button btn : buttons) {
			btn.setPrefWidth(200);
		}
		Button back = new Button("Back");
		back.setOnAction(e-> l.showLoginForm(stage));

		// Layout for dashboard buttons
		GridPane dashboardButtons = new GridPane();
		dashboardButtons.setHgap(15);
		dashboardButtons.setVgap(15);
		dashboardButtons.setPadding(new Insets(10));
		dashboardButtons.setAlignment(Pos.CENTER);

		dashboardButtons.add(viewProductsBtn, 0, 0);
		dashboardButtons.add(lowStockReportBtn, 1, 0);
		dashboardButtons.add(calcStockValueBtn, 0, 1);
		dashboardButtons.add(viewSuppliers, 1, 1);
		dashboardButtons.add(showTransactions, 0, 2);
		dashboardButtons.add(viewReportBtn, 1, 2);
		//dashboardButtons.add(back, 1, 4);
		dashboardButtons.setStyle("-fx-background-color: skyblue;");

		// TitledPane to collapse/expand the dashboard buttons
		TitledPane dashboardPane = new TitledPane("▶ Dashboard", dashboardButtons);
		dashboardPane.setExpanded(false); // Start collapsed
		dashboardPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
			dashboardPane.setText((isNowExpanded ? "▼" : "▶") + " Dashboard");
		});
        //Button back = new Button("Go Back");
        //back.setOnAction(e-> );
        
		// Root layout
		VBox root = new VBox(20,back,  dashboardPane );
		root.setStyle("-fx-background-color: skyblue;");
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.TOP_CENTER);
		Scene sc = new Scene(root, 500, 500);
		stage.setScene(sc);
		stage.setTitle("INVENTORY MANAGEMENT SYSTEM");
		stage.show();
		sc.setFill(Color.BLACK);

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

	    // Use DAO to fetch real data from the database
	    ProductDAO productDAO = new ProductDAO();
	    SupplierDAO supplierDAO = new SupplierDAO();
	    TransactionDAO transactionDAO = new TransactionDAO();

	    productDAO.getAllProducts().forEach(p -> p.accept(reportVisitor));
	    supplierDAO.getAllSuppliers().forEach(s -> s.accept(reportVisitor));
	    transactionDAO.getAllTransactions().forEach(t -> t.accept(reportVisitor));

	    // Create tables with summary strings
	    TableView<String> productTable = createTable(reportVisitor.getProductSummaries(), "Product Summary");
	    TableView<String> supplierTable = createTable(reportVisitor.getSupplierSummaries(), "Supplier Summary");
	    TableView<String> transactionTable = createTable(reportVisitor.getTransactionSummaries(), "Transaction Summary");

	    // Tab view
	    TabPane tabPane = new TabPane();
	    tabPane.getTabs().addAll(
	        new Tab("Products", productTable),
	        new Tab("Suppliers", supplierTable),
	        new Tab("Transactions", transactionTable)
	    );
	    tabPane.getTabs().forEach(tab -> tab.setClosable(false));

	    // Back button
	    Button backBtn = new Button("Back");
	    backBtn.setOnAction(e -> showDashboard(stage));

	    VBox layout = new VBox(10, tabPane, backBtn);
	    layout.setPadding(new Insets(20));

	    stage.setScene(new Scene(layout, 800, 600));
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

        Label header = new Label("Low Stock Report");

        TableView<Product> tableView = new TableView<>(lowStockProducts);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> thresholdCol = new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));

        tableView.getColumns().addAll(nameCol, qtyCol, thresholdCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Message if no low stock found
        Label infoLabel = new Label();
        if (lowStockProducts.isEmpty()) {
            infoLabel.setText("All products are above threshold.");
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showDashboard(stage));

        VBox layout = new VBox(10, header, tableView, infoLabel, backBtn);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
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
	    // Create DAO and fetch data from the database
	    SupplierDAO supplierDAO = new SupplierDAO();
	    List<Supplier> supplierListFromDB = supplierDAO.getAllSuppliers();

	    // Observable list for TableView
	    ObservableList<Supplier> supplierList = FXCollections.observableArrayList(supplierListFromDB);

	    // TableView and columns
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

	    // Add columns to the TableView
	    tableView.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, addressCol);
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    // Back button
	    Button backBtn = new Button("Back");
	    backBtn.setOnAction(e -> showDashboard(stage));

	    // Add Supplier button
	    Button addSupplierBtn = new Button("Add Supplier");
	    SupplierController supplierController = new SupplierController();
	    addSupplierBtn.setOnAction(e -> supplierController.showAddSupplierForm(stage));

	    // Refresh button (optional but helpful)
	    Button refreshBtn = new Button("Refresh");
	    refreshBtn.setOnAction(e -> {
	        List<Supplier> refreshedList = new SupplierDAO().getAllSuppliers();
	        tableView.setItems(FXCollections.observableArrayList(refreshedList));
	    });

	    // Layout
	    HBox buttons = new HBox(10, backBtn, addSupplierBtn, refreshBtn);
	    VBox root = new VBox(10, tableView, buttons);
	    root.setStyle("-fx-padding: 20");

	    // Show scene
	    Scene scene = new Scene(root, 800, 450);
	    stage.setScene(scene);
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
	    List<Transaction> supplierListFromDB = transactionDAO.getAllTransactions();
		List<Transaction> transactions = TransactionData.getAllTransactions();
		ObservableList<Transaction> transactionList = FXCollections.observableArrayList(supplierListFromDB);

		TableView<Transaction> tableView = new TableView<>(transactionList);

		TableColumn<Transaction, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));

		TableColumn<Transaction, String> productCol = new TableColumn<>("Product");
		productCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getProduct().getName()));

		TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().toString()));

		TableColumn<Transaction, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));

		TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTimestamp().toString()));

		tableView.getColumns().addAll(idCol, productCol, typeCol, qtyCol, dateCol);

		Button backBtn = new Button("Back");
		backBtn.setOnAction(e -> showDashboard(stage));
		Button addTransaction = new Button("Add Transaction");
		c = new TransactionController();

		addTransaction.setOnAction(e -> c.showAddTransactionForm(stage));

		VBox root = new VBox(10, tableView, backBtn, addTransaction);
		root.setStyle("-fx-padding: 20");

		stage.setScene(new Scene(root, 700, 400));
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

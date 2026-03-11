/*
 * Purpose:
 * - controls the product management screen for owners and managers
 * 
 * Function:
 * - displays products in a TableView
 * - creates new product records from form input
 * - updates the selected product
 * - loads selected product values back into the form
 * - refreshes product list from the database
 * - returns user to the main menu
 * 
 * Dependencies:
 * - ProductService for product business logic
 * - Router for navigation
 * - Product domain object
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductManagementController {

    @FXML private TextField nameField; // product name input
    @FXML private TextField categoryField; // product category input
    @FXML private TextField quantityField; // quantity on hand input
    @FXML private TextField reorderThresholdField; // reorder threshold input
    @FXML private CheckBox activeCheckBox; // product active flag

    @FXML private TableView<Product> productTable; // displays products
    @FXML private TableColumn<Product, Long> colId; // product id column
    @FXML private TableColumn<Product, String> colName; // product name column
    @FXML private TableColumn<Product, String> colCategory; // product category column
    @FXML private TableColumn<Product, Integer> colQuantity; // quantity on hand column
    @FXML private TableColumn<Product, Integer> colReorderThreshold; // reorder threshold column
    @FXML private TableColumn<Product, Boolean> colActive; // active status column

    @FXML private Label messageLabel; // displays validation and status messages

    private final ProductService productService = new ProductService(); // product business logic
    private final ObservableList<Product> productRows = FXCollections.observableArrayList(); // table backing list

    // Method - initialize product management screen
    @FXML
    private void initialize() {
        // bind observable product list to table
        productTable.setItems(productRows);

        // map table columns to Product values
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colCategory.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory()));
        colQuantity.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantityOnHand()));
        colReorderThreshold.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getReorderThreshold()));
        colActive.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().isActive()));

        // resize columns to fill available table width
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // load selected row values into form for editing
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadSelectedProductIntoForm(newSelection);
            }
        });

        // load initial product list
        refresh();
    }

    // Method - create new product from form input
    @FXML
    private void onCreateProduct() {
        try {
            // read product values from form
            String name = nameField.getText();
            String category = categoryField.getText();
            int quantity = parseInt(quantityField.getText(), "Quantity must be a valid whole number.");
            int reorderThreshold = parseInt(reorderThresholdField.getText(), "Reorder threshold must be a valid whole number.");
            boolean active = activeCheckBox.isSelected();

            // create product through service layer
            productService.createProduct(name, category, quantity, reorderThreshold, active);

            // show success message, clear form, and reload table
            messageLabel.setText("Product created.");
            clearForm();
            refresh();

        } catch (Exception ex) { // create product failure
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - update selected product from form input
    @FXML
    private void onUpdateProduct() {
        // require selected product row before updating
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a product first.");
            return;
        }

        try {
            // read updated product values from form
            String name = nameField.getText();
            String category = categoryField.getText();
            int quantity = parseInt(quantityField.getText(), "Quantity must be a valid whole number.");
            int reorderThreshold = parseInt(reorderThresholdField.getText(), "Reorder threshold must be a valid whole number.");
            boolean active = activeCheckBox.isSelected();

            // update selected product through service layer
            productService.updateProduct(selected.getId(), name, category, quantity, reorderThreshold, active);

            // show success message, clear form, and reload table
            messageLabel.setText("Product updated.");
            clearForm();
            refresh();

        } catch (Exception ex) { // update product failure
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - reload product table from database
    @FXML
    private void onRefresh() {
        refresh();
        messageLabel.setText("Product list refreshed.");
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }

    // Method - load selected product values into form controls
    private void loadSelectedProductIntoForm(Product product) {
        nameField.setText(product.getName());
        categoryField.setText(product.getCategory());
        quantityField.setText(String.valueOf(product.getQuantityOnHand()));
        reorderThresholdField.setText(String.valueOf(product.getReorderThreshold()));
        activeCheckBox.setSelected(product.isActive());
    }

    // Method - refresh product table rows
    private void refresh() {
        productRows.setAll(productService.listProducts());
    }

    // Method - clear form fields after create or update
    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        quantityField.clear();
        reorderThresholdField.clear();
        activeCheckBox.setSelected(true);
        productTable.getSelectionModel().clearSelection();
    }

    // Method - parse integer input from text field
    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (Exception e) { // invalid integer input
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
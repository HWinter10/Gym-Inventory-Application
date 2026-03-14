/*
 * Purpose:
 * - owner and manager UI for managing product catalog
 * 
 * Function:
 * - displays product list in TableView
 * - creates new products
 * - updates selected products
 * - reloads product list from database
 * - returns to main menu
 * 
 * Dependencies:
 * - ProductService for product business logic
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductManagementController {

    @FXML private TextField nameField; // product name 
    @FXML private TextField categoryField; // product category input
    @FXML private TextField quantityField; // starting quantity input (new products only)
    @FXML private TextField reorderThresholdField; // reorder threshold input
    @FXML private CheckBox activeCheckBox; // product active flag
    @FXML private Label currentQuantityLabel; // displays existing quantity on hand for selected product

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
        productTable.setItems(productRows);

        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colCategory.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory()));
        colQuantity.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantityOnHand()));
        colReorderThreshold.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getReorderThreshold()));
        colActive.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().isActive()));

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadSelectedProductIntoForm(newSelection);
            }
        });

        clearForm();
        refresh();
    }

    // Method - create new product from form input
    @FXML
    private void onCreateProduct() {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            int quantity = parseInt(quantityField.getText(), "Starting quantity must be a valid whole number.");
            int reorderThreshold = parseInt(reorderThresholdField.getText(), "Reorder threshold must be a valid whole number.");
            boolean active = activeCheckBox.isSelected();

            productService.createProduct(name, category, quantity, reorderThreshold, active);

            messageLabel.setText("Product created.");
            clearForm();
            refresh();

        } catch (Exception ex) { // service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - update selected product catalog details without changing quantity on hand
    @FXML
    private void onUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a product first.");
            return;
        }

        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            int reorderThreshold = parseInt(reorderThresholdField.getText(), "Reorder threshold must be a valid whole number.");
            boolean active = activeCheckBox.isSelected();

            productService.updateProduct(selected.getId(), name, category, reorderThreshold, active);

            messageLabel.setText("Product updated. Quantity on hand is managed through Sales Entry and Inventory Adjustment.");
            refresh();

            Product refreshedSelected = null;
            for (Product product : productRows) {
                if (product.getId() == selected.getId()) {
                    refreshedSelected = product;
                    break;
                }
            }

            if (refreshedSelected != null) {
                productTable.getSelectionModel().select(refreshedSelected);
                loadSelectedProductIntoForm(refreshedSelected);
            } else {
                clearForm();
            }

        } catch (Exception ex) { // service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - reload product table from database
    @FXML
    private void onRefresh() {
        refresh();
        messageLabel.setText("Product list refreshed.");
    }

    // Method - clear form for new product entry
    @FXML
    private void onClearForm() {
        clearForm();
        messageLabel.setText("");
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
        reorderThresholdField.setText(Integer.toString(product.getReorderThreshold()));
        activeCheckBox.setSelected(product.isActive());

        currentQuantityLabel.setText("Current quantity on hand: " + product.getQuantityOnHand());
        quantityField.clear();
        quantityField.setPromptText("Starting quantity used only when creating a new product");
    }

    // Method - refresh product table rows
    private void refresh() {
        productRows.setAll(productService.listProducts());
    }

    // Method - clear form fields after create or when preparing new entry
    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        quantityField.clear();
        reorderThresholdField.clear();
        activeCheckBox.setSelected(true);
        currentQuantityLabel.setText("Current quantity on hand: n/a");
        quantityField.setPromptText("Starting quantity for new product");
        productTable.getSelectionModel().clearSelection();
    }

    // Method - parse integer input from text field
    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (Exception e) { // invalid numeric input
            throw new IllegalArgumentException(errorMessage);
        }
    }
}

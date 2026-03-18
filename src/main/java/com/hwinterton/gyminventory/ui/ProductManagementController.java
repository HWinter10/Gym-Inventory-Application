/*
 * Purpose:
 * - owner and manager UI for managing product catalog
 *
 * Function:
 * - displays product list in TableView
 * - creates new products
 * - updates selected products including active status
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductManagementController {

    @FXML private TextField createNameField; // create product name input
    @FXML private TextField createCategoryField; // create product category input
    @FXML private TextField createQuantityField; // create quantity input
    @FXML private TextField createReorderThresholdField; // create reorder threshold input

    @FXML private TextField editNameField; // edit product name input
    @FXML private TextField editCategoryField; // edit product category input
    @FXML private TextField editQuantityField; // edit quantity input
    @FXML private TextField editReorderThresholdField; // edit reorder threshold input
    @FXML private CheckBox editActiveCheckBox; // edit active flag

    @FXML private TableView<Product> productTable; // product table
    @FXML private TableColumn<Product, Long> colId; // product id column
    @FXML private TableColumn<Product, String> colName; // product name column
    @FXML private TableColumn<Product, String> colCategory; // product category column
    @FXML private TableColumn<Product, Integer> colQuantity; // quantity on hand column
    @FXML private TableColumn<Product, Integer> colReorderThreshold; // reorder threshold column
    @FXML private TableColumn<Product, Boolean> colActive; // active status column

    @FXML private Label messageLabel; // status and validation message

    private final ProductService productService = new ProductService(); // product business logic
    private final ObservableList<Product> productRows = FXCollections.observableArrayList(); // table backing list

    // Method - initialize product management screen
    @FXML
    private void initialize() {
        hideMessage();

        productTable.setItems(productRows);

        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colCategory.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        colQuantity.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getQuantityOnHand()));
        colReorderThreshold.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getReorderThreshold()));
        colActive.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().isActive()));

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadSelectedProductIntoForm(newSelection);
            } else {
                clearEditForm();
            }
        });

        refresh();
    }

    // Method - create new product from form input
    @FXML
    private void onCreateProduct() {
        try {
            hideMessage();

            String name = createNameField.getText();
            String category = createCategoryField.getText();
            int quantity = parseInt(createQuantityField.getText(), "Quantity must be a valid whole number.");
            int reorderThreshold = parseInt(createReorderThresholdField.getText(), "Reorder level must be a valid whole number.");

            productService.createProduct(name, category, quantity, reorderThreshold, true);

            showSuccess("Product created successfully.");
            clearCreateForm();
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - update selected product from form input
    @FXML
    private void onUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a product to update.");
            return;
        }

        try {
            hideMessage();

            String name = editNameField.getText();
            String category = editCategoryField.getText();
            int quantity = parseInt(editQuantityField.getText(), "Quantity must be a valid whole number.");
            int reorderThreshold = parseInt(editReorderThresholdField.getText(), "Reorder level must be a valid whole number.");
            boolean active = editActiveCheckBox.isSelected();

            productService.updateProduct(selected.getId(), name, category, quantity, reorderThreshold, active);

            showSuccess("Product updated successfully.");
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - reload product table from database
    @FXML
    private void onRefresh() {
        try {
            refresh();
            showSuccess("Product list refreshed.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }

    // Method - load selected product into edit form
    private void loadSelectedProductIntoForm(Product product) {
        editNameField.setText(product.getName());
        editCategoryField.setText(product.getCategory());
        editQuantityField.setText(String.valueOf(product.getQuantityOnHand()));
        editReorderThresholdField.setText(String.valueOf(product.getReorderThreshold()));
        editActiveCheckBox.setSelected(product.isActive());
    }

    // Method - refresh product rows
    private void refresh() {
        productRows.setAll(productService.listProducts());
    }

    // Method - clear create form
    private void clearCreateForm() {
        createNameField.clear();
        createCategoryField.clear();
        createQuantityField.clear();
        createReorderThresholdField.clear();
    }

    // Method - clear edit form
    private void clearEditForm() {
        editNameField.clear();
        editCategoryField.clear();
        editQuantityField.clear();
        editReorderThresholdField.clear();
        editActiveCheckBox.setSelected(true);
    }

    // Method - parse integer from text input
    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // Method - show success message
    private void showSuccess(String text) {
        showMessage(text, "message-success");
    }

    // Method - show error message
    private void showError(String text) {
        showMessage(text, "message-error");
    }

    // Method - apply message style and text
    private void showMessage(String text, String styleClass) {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setText(text);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    // Method - clear and hide message
    private void hideMessage() {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
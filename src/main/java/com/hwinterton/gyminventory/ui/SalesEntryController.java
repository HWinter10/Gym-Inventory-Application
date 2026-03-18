/*
 * Purpose:
 * - controls quick sales entry screen
 * 
 * Function:
 * - loads category list
 * - records submitted sale
 * - refreshes available products after inventory update
 * - returns to main menu
 * 
 * Dependencies:
 * - SalesService for business logic
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.service.SalesService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class SalesEntryController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private Label availableQuantityLabel;
    @FXML private Label messageLabel;

    private final SalesService salesService = new SalesService();

    // Method - initialize sales entry screen
    @FXML
    private void initialize() {
        hideMessage();
        loadCategories();

        productComboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        productComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        categoryComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            loadProductsForCategory(newValue);
            availableQuantityLabel.setText("Select a product to view current stock.");
        });

        productComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                availableQuantityLabel.setText("Select a product to view current stock.");
            } else {
                availableQuantityLabel.setText("Current stock: " + newValue.getQuantityOnHand());
            }
        });
    }

    // Method - load categories from service into category combo box
    private void loadCategories() {
        List<String> categories = salesService.listCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    // Method - load products for selected category into product combo box
    private void loadProductsForCategory(String category) {
        if (category == null || category.isBlank()) {
            productComboBox.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Product> products = salesService.listProductsByCategory(category);
        productComboBox.setItems(FXCollections.observableArrayList(products));
    }

    // Method - handle sale submission
    @FXML
    private void onSubmitSale() {
        try {
            hideMessage();

            Product selectedProduct = productComboBox.getValue();
            if (selectedProduct == null) {
                showError("Please select a product.");
                return;
            }

            int quantity = parseInt(quantityField.getText(), "Quantity must be a valid whole number.");

            salesService.recordSale(selectedProduct.getId(), quantity);

            showSuccess("Sale recorded successfully.");
            quantityField.clear();

            String selectedCategory = categoryComboBox.getValue();
            loadProductsForCategory(selectedCategory);

            for (Product product : productComboBox.getItems()) {
                if (product.getId() == selectedProduct.getId()) {
                    productComboBox.setValue(product);
                    availableQuantityLabel.setText("Current stock: " + product.getQuantityOnHand());
                    return;
                }
            }

            productComboBox.getSelectionModel().clearSelection();
            availableQuantityLabel.setText("Select a product to view current stock.");

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - clear entered sale values
    @FXML
    private void onClear() {
        categoryComboBox.getSelectionModel().clearSelection();
        productComboBox.setItems(FXCollections.observableArrayList());
        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
        availableQuantityLabel.setText("Select a product to view current stock.");
        hideMessage();
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }

    // Method - parse integer input from text field
    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // Method - display success message
    private void showSuccess(String text) {
        showMessage(text, "message-success");
    }

    // Method - display error message
    private void showError(String text) {
        showMessage(text, "message-error");
    }

    // Method - display styled message
    private void showMessage(String text, String styleClass) {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setText(text);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    // Method - hide message label
    private void hideMessage() {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
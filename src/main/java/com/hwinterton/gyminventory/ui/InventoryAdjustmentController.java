/*
 * Purpose:
 * - controls inventory adjustment screen
 * 
 * Function:
 * - loads category list
 * - filters products by selected category
 * - records inventory adjustments with required reason code
 * - refreshes available quantity after update
 * - returns to main menu
 * 
 * Dependencies:
 * - InventoryAdjustmentService for business logic
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.AdjustmentReason;
import com.hwinterton.gyminventory.domain.Product;
import com.hwinterton.gyminventory.service.InventoryAdjustmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class InventoryAdjustmentController {

    @FXML private ComboBox<String> categoryComboBox; // category filter
    @FXML private ComboBox<Product> productComboBox; // product selector
    @FXML private ComboBox<String> directionComboBox; // increase or decrease selector
    @FXML private TextField amountField; // adjustment amount input
    @FXML private ComboBox<AdjustmentReason> reasonComboBox; // required reason code selector
    @FXML private TextArea notesArea; // optional notes
    @FXML private Label availableQuantityLabel; // shows current quantity on hand
    @FXML private Label messageLabel; // displays status and validation messages

    private final InventoryAdjustmentService adjustmentService = new InventoryAdjustmentService(); // adjustment workflow logic

    // Method - initialize inventory adjustment screen
    @FXML
    private void initialize() {
        loadCategories();

        directionComboBox.setItems(FXCollections.observableArrayList("Increase", "Decrease"));
        reasonComboBox.setItems(FXCollections.observableArrayList(AdjustmentReason.values()));

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
            availableQuantityLabel.setText("Available quantity: ");
        });

        productComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                availableQuantityLabel.setText("Available quantity: ");
            } else {
                availableQuantityLabel.setText("Available quantity: " + newValue.getQuantityOnHand());
            }
        });
    }

    // Method - refresh categories from database
    private void loadCategories() {
        List<String> categories = adjustmentService.listCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    // Method - refresh products for selected category
    private void loadProductsForCategory(String category) {
        if (category == null || category.isBlank()) {
            productComboBox.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Product> products = adjustmentService.listProductsByCategory(category);
        productComboBox.setItems(FXCollections.observableArrayList(products));
    }

    // Method - handle adjustment submission
    @FXML
    private void onSubmitAdjustment() {
        try {
            Product selectedProduct = productComboBox.getValue();
            if (selectedProduct == null) {
                messageLabel.setText("Select a product.");
                return;
            }

            String direction = directionComboBox.getValue();
            AdjustmentReason reason = reasonComboBox.getValue();
            int amount = parseInt(amountField.getText(), "Amount must be a valid whole number.");
            String notes = notesArea.getText();

            adjustmentService.recordAdjustment(selectedProduct.getId(), direction, amount, reason, notes);

            messageLabel.setText("Inventory adjustment recorded.");
            amountField.clear();
            notesArea.clear();

            String selectedCategory = categoryComboBox.getValue();
            loadProductsForCategory(selectedCategory);

            for (Product product : productComboBox.getItems()) {
                if (product.getId() == selectedProduct.getId()) {
                    productComboBox.setValue(product);
                    availableQuantityLabel.setText("Available quantity: " + product.getQuantityOnHand());
                    return;
                }
            }

            productComboBox.getSelectionModel().clearSelection();
            availableQuantityLabel.setText("Available quantity: ");

        } catch (Exception ex) { // display service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - clear entered values
    @FXML
    private void onClear() {
        categoryComboBox.getSelectionModel().clearSelection();
        productComboBox.setItems(FXCollections.observableArrayList());
        productComboBox.getSelectionModel().clearSelection();
        directionComboBox.getSelectionModel().clearSelection();
        reasonComboBox.getSelectionModel().clearSelection();
        amountField.clear();
        notesArea.clear();
        availableQuantityLabel.setText("Available quantity: ");
        messageLabel.setText("");
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
        } catch (Exception e) { // invalid numeric input
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
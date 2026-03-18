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
    @FXML private ComboBox<AdjustmentReason> reasonComboBox; // required reason selector
    @FXML private TextArea notesArea; // optional notes
    @FXML private Label availableQuantityLabel; // current quantity display
    @FXML private Label messageLabel; // status and validation message

    private final InventoryAdjustmentService adjustmentService = new InventoryAdjustmentService(); // adjustment workflow logic

    // Method - initialize inventory adjustment screen
    @FXML
    private void initialize() {
        hideMessage();
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

    // Method - load categories from service
    private void loadCategories() {
        List<String> categories = adjustmentService.listCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    // Method - load products for selected category
    private void loadProductsForCategory(String category) {
        if (category == null || category.isBlank()) {
            productComboBox.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Product> products = adjustmentService.listProductsByCategory(category);
        productComboBox.setItems(FXCollections.observableArrayList(products));
    }

    // Method - submit inventory adjustment
    @FXML
    private void onSubmitAdjustment() {
        try {
            hideMessage();

            Product selectedProduct = productComboBox.getValue();
            if (selectedProduct == null) {
                showError("Please select a product.");
                return;
            }

            String direction = directionComboBox.getValue();
            AdjustmentReason reason = reasonComboBox.getValue();
            int amount = parseInt(amountField.getText(), "Amount must be a valid whole number.");
            String notes = notesArea.getText();

            adjustmentService.recordAdjustment(selectedProduct.getId(), direction, amount, reason, notes);

            showSuccess("Inventory adjustment recorded successfully.");
            amountField.clear();
            notesArea.clear();

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

    // Method - clear form values
    @FXML
    private void onClear() {
        categoryComboBox.getSelectionModel().clearSelection();
        productComboBox.setItems(FXCollections.observableArrayList());
        productComboBox.getSelectionModel().clearSelection();
        directionComboBox.getSelectionModel().clearSelection();
        reasonComboBox.getSelectionModel().clearSelection();
        amountField.clear();
        notesArea.clear();
        availableQuantityLabel.setText("Select a product to view current stock.");
        hideMessage();
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
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
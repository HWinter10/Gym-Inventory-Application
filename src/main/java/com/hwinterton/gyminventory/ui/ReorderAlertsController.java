/*
 * Purpose:
 * - controls reorder alerts screen
 * 
 * Function:
 * - loads reorder alert list into table
 * - refreshes reorder alert view
 * - returns to main menu
 * 
 * Dependencies:
 * - ReorderService for reorder alert logic
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.ReorderAlert;
import com.hwinterton.gyminventory.service.ReorderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReorderAlertsController {

    @FXML private TableView<ReorderAlert> reorderTable; // displays reorder alerts
    @FXML private TableColumn<ReorderAlert, Long> colProductId; // product id column
    @FXML private TableColumn<ReorderAlert, String> colProductName; // product name column
    @FXML private TableColumn<ReorderAlert, String> colCategory; // category column
    @FXML private TableColumn<ReorderAlert, Integer> colQuantityOnHand; // quantity on hand column
    @FXML private TableColumn<ReorderAlert, Integer> colReorderThreshold; // reorder threshold column
    @FXML private TableColumn<ReorderAlert, String> colStatus; // alert status column
    @FXML private Label messageLabel; // screen status message

    private final ReorderService reorderService = new ReorderService(); // reorder alert logic
    private final ObservableList<ReorderAlert> reorderRows = FXCollections.observableArrayList(); // table backing list

    // Method - initialize reorder alerts screen
    @FXML
    private void initialize() {
        hideMessage();

        reorderTable.setItems(reorderRows);

        colProductId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getProductId()));
        colProductName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProductName()));
        colCategory.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory()));
        colQuantityOnHand.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantityOnHand()));
        colReorderThreshold.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getReorderThreshold()));
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                getStyleClass().removeAll("status-low", "status-threshold", "status-normal");

                if (empty || status == null) {
                    setText(null);
                    return;
                }

                setText(status);

                if ("LOW".equalsIgnoreCase(status)) {
                    getStyleClass().add("status-low");
                } else if ("AT THRESHOLD".equalsIgnoreCase(status)) {
                    getStyleClass().add("status-threshold");
                } else {
                    getStyleClass().add("status-normal");
                }
            }
        });

        reorderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refresh();
    }

    // Method - reload reorder alerts
    @FXML
    private void onRefresh() {
        refresh();
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }

    // Method - refresh reorder alert rows
    private void refresh() {
        reorderRows.setAll(reorderService.getReorderAlerts());

        if (reorderRows.isEmpty()) {
            showSuccess("All products are sufficiently stocked.");
        } else {
            showError(reorderRows.size() + " products require attention.");
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
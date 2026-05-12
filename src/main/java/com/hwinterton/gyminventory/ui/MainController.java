package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controls the main menu screen.
 *
 * <p>This controller displays the signed-in user information, enables or
 * disables buttons based on role, and routes button actions to the correct
 * JavaFX screens.</p>
 *
 * <p>Uses {@link SessionManager} for current user information and {@link Router}
 * for screen navigation.</p>
 */
public class MainController {

    @FXML private Label welcomeLabel; // displays logged in user information
    @FXML private Label roleLabel; // displays current user role

    @FXML private Button manageUsersButton; // navigates to user management screen
    @FXML private Button manageProductsButton; // navigates to product management screen
    @FXML private Button actionLogButton; // navigates to action log viewer screen (controlled audit log view)
    @FXML private Button salesEntryButton; // navigates to sales entry screen
    @FXML private Button inventoryAdjustmentButton; // navigates to inventory adjustment screen
    @FXML private Button reorderAlertsButton; // navigates to reorder alerts screen

    /**
     * Initializes main menu after FXML loads
     * 
     * <p>Displays current user and applies role-based button access</p>
     */
    @FXML
    private void initialize() {
        User user = SessionManager.getUser();

        if (user == null) {
            return;
        }

        welcomeLabel.setText("Signed in as: " + user.getUsername());
        roleLabel.setText("Access level: " + user.getRole());

        boolean isOwner = user.getRole() == Role.OWNER;
        boolean canManageProducts = user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;

        // owner-only screens
        if (manageUsersButton != null) {
            manageUsersButton.setDisable(!isOwner);
        }
        
        if (actionLogButton != null) {
            actionLogButton.setDisable(!isOwner);
        }

        // owner and manager-only screens
        if (manageProductsButton != null) {
            manageProductsButton.setDisable(!canManageProducts);
        }

        // daily tasks screens available to logged-in users
        if (salesEntryButton != null) {
            salesEntryButton.setDisable(false);
        }

        if (inventoryAdjustmentButton != null) {
            inventoryAdjustmentButton.setDisable(false);
        }

        if (reorderAlertsButton != null) {
            reorderAlertsButton.setDisable(false);
        }
    }

    // FXML event - user management button
    @FXML
    private void onManageUsers() {
        Router.showUserManagement();
    }

    // FXML event - product management button action
    @FXML
    private void onManageProducts() {
        Router.showProductManagement();
    }
    
    // FXML event - action log button
    @FXML
    private void onActionLog() {
    	Router.showActionLog();
    }

    // FXML event - sales entry button
    @FXML
    private void onSalesEntry() {
        Router.showSalesEntry();
    }

    // FXML event - inventory adjustment button
    @FXML
    private void onInventoryAdjustment() {
        Router.showInventoryAdjustment();
    }

    // FXML event - reorder alerts button
    @FXML
    private void onReorderAlerts() {
        Router.showReorderAlerts();
    }

    // FXML event - logout button
    @FXML
    private void onLogout() {
        SessionManager.clear();
        Router.showLogin();
    }
}
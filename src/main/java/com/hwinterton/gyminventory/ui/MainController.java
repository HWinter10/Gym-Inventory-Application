/*
 * Purpose:
 * - controls main menu screen
 * 
 * Function:
 * - displays logged in user info
 * - enables or disables buttons based on role
 * - route to user management, product management, and sales entry screens
 * 
 * Dependencies:
 * - SessionManager to get current user
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

	// labels
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // buttons
    @FXML private Button manageUsersButton;
    @FXML private Button manageProductsButton;
    @FXML private Button salesEntryButton;
    @FXML private Button inventoryAdjustmentButton;

    // Method - initialize main menu after FXML load
    @FXML
    private void initialize() {

        User user = SessionManager.getUser();

        if (user == null) {
            Router.showLogin();
            return;
        }

        // display current user information
        welcomeLabel.setText("Logged in as: " + user.getUsername());
        roleLabel.setText("Role: " + user.getRole());

        boolean isOwner = user.getRole() == Role.OWNER;
        boolean canManageProducts = user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;

        // enable or disable menu options based on role
        if (manageUsersButton != null) {
            manageUsersButton.setDisable(!isOwner);
        }

        if (manageProductsButton != null) {
            manageProductsButton.setDisable(!canManageProducts);
        }

        if (salesEntryButton != null) {
            salesEntryButton.setDisable(false);
        }

        if (inventoryAdjustmentButton != null) {
            inventoryAdjustmentButton.setDisable(false);
        }
    }

    // Method - open user management screen (owner only)
    @FXML
    private void onManageUsers() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        if (user.getRole() != Role.OWNER) {
            return;
        }

        Router.showUserManagement();
    }

    // Method - open product management screen
    @FXML
    private void onManageProducts() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            return;
        }

        Router.showProductManagement();
    }

    // Method - open sales entry screen
    @FXML
    private void onSalesEntry() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        Router.showSalesEntry();
    }

    // Method - open inventory adjustment screen
    @FXML
    private void onInventoryAdjustment() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        Router.showInventoryAdjustment();
    }

    // Method - logout current user, return to login screen
    @FXML
    private void onLogout() {
        SessionManager.clear();
        Router.showLogin();
    }
}

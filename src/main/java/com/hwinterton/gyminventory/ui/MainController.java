/*
 * Purpose:
 * - controls main menu screen
 * 
 * Function:
 * - displays logged in user info
 * - enables or disables buttons based on role
 * - routes to screens like User Management, Product Management, Sales Entry, Inventory Adjustment, and Reorder Alerts
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

    @FXML private Label welcomeLabel; // displays logged in user information
    @FXML private Label roleLabel; // displays current user role

    @FXML private Button manageUsersButton; // navigates to user management screen
    @FXML private Button manageProductsButton; // navigates to product management screen
    @FXML private Button salesEntryButton; // navigates to sales entry screen
    @FXML private Button inventoryAdjustmentButton; // navigates to inventory adjustment screen
    @FXML private Button reorderAlertsButton; // navigates to reorder alerts screen

    // Method - initialize main menu after FXML load
    @FXML
    private void initialize() {

        User user = SessionManager.getUser();

        if (user == null) {
            Router.showLogin();
            return;
        }

        welcomeLabel.setText("Logged in as: " + user.getUsername());
        roleLabel.setText("Role: " + user.getRole());

        boolean isOwner = user.getRole() == Role.OWNER;
        boolean canManageProducts = user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;

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

        if (reorderAlertsButton != null) {
            reorderAlertsButton.setDisable(false);
        }
    }

    // Method - handle user management button action
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

    // Method - handle product management button action
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

    // Method - handle sales entry button action
    @FXML
    private void onSalesEntry() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        Router.showSalesEntry();
    }

    // Method - handle inventory adjustment button action
    @FXML
    private void onInventoryAdjustment() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        Router.showInventoryAdjustment();
    }

    // Method - handle reorder alerts button action
    @FXML
    private void onReorderAlerts() {
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        Router.showReorderAlerts();
    }

    // Method - handle logout action
    @FXML
    private void onLogout() {
        SessionManager.clear();
        Router.showLogin();
    }
}
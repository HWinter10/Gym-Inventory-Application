/*
 * Purpose:
 * - controls main menu screen
 * 
 * Function:
 * - displays logged in user info
 * - enables or disables buttons based on role
 * - routes to screens like User Management
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

    // Method - initialize main menu after FXML load
    @FXML
    private void initialize() {

        // get current logged in user
        User user = SessionManager.getUser();

        // redirect to login if session missing
        if (user == null) {
            Router.showLogin();
            return;
        }

        // display username and role in labels
        welcomeLabel.setText("Logged in as: " + user.getUsername());
        roleLabel.setText("Role: " + user.getRole());

        // determine owner role for permission checks
        boolean isOwner = user.getRole() == Role.OWNER;
        boolean canManageProducts = user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;

        // disable user management for non owners
        if (manageUsersButton != null) {
            manageUsersButton.setDisable(!isOwner);
        }

        // disable product management for staff
        if (manageProductsButton != null) {
            manageProductsButton.setDisable(!canManageProducts);
        }
    }

    // Method - handle user management button action
    @FXML
    private void onManageUsers() {

        // confirm active session
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        // restrict access to owner role
        if (user.getRole() != Role.OWNER) {
            return;
        }

        // navigate to user management screen
        Router.showUserManagement();
    }

    // Method - handle product management button action
    @FXML
    private void onManageProducts() {

        // confirm active session
        User user = SessionManager.getUser();
        if (user == null) {
            Router.showLogin();
            return;
        }

        // restrict access to owners and managers
        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            return;
        }

        // navigate to product management screen
        Router.showProductManagement();
    }

    // Method - handle logout action
    @FXML
    private void onLogout() {

        // clear session user
        SessionManager.clear();

        // return to login screen
        Router.showLogin();
    }
}
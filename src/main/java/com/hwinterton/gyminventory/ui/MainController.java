package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.AuthorizationService;
import com.hwinterton.gyminventory.security.SessionManager;
import com.hwinterton.gyminventory.service.AuditService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private Button manageUsersButton;
    @FXML private Button manageProductsButton;

    private final AuditService auditService = new AuditService();
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;

        welcomeLabel.setText("Welcome, " + user.getUsername());
        roleLabel.setText("Role: " + user.getRole());

        manageUsersButton.setDisable(!AuthorizationService.canManageUsers(user));

        boolean canManageProducts = user.getRole() == Role.OWNER || user.getRole() == Role.MANAGER;
        manageProductsButton.setDisable(!canManageProducts);
    }

    @FXML
    private void onManageUsers() {
        Router.showUserManagement();
    }

    @FXML
    private void onManageProducts() {
        System.out.println("Manage Products clicked by " + currentUser.getRole());
    }

    @FXML
    private void onLogout() {
        if (SessionManager.isLoggedIn()) {
            var user = SessionManager.getUser();
            auditService.log(user.getId(), "LOGOUT", "User logged out");
        }
        SessionManager.clear();
        Router.showLogin();
    }
}
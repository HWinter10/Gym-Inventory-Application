package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.security.AuthorizationService;
import com.hwinterton.gyminventory.security.SessionManager;
import com.hwinterton.gyminventory.service.AuditService;
import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button manageProductsButton;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;

        welcomeLabel.setText("Welcome, " + user.getUsername());
        roleLabel.setText("Role: " + user.getRole());

        boolean canManage =
                user.getRole() == Role.OWNER ||
                user.getRole() == Role.MANAGER;

        manageProductsButton.setDisable(!canManage);
    }

    private final AuditService auditService = new AuditService();
    
    @FXML
    private void onLogout() {
        if (SessionManager.isLoggedIn()) {
            var user = SessionManager.getUser();
            auditService.log(user.getId(), "LOGOUT", "User logged out");
        }
        SessionManager.clear();
        Router.showLogin();
    }

    @FXML
    private void onManageProducts() {
        var user = SessionManager.getUser();
        AuthorizationService.requireAnyRole(user,
                Role.OWNER,
                Role.MANAGER
        );
        System.out.println("Manage Products clicked by " + user.getRole());
    }
}
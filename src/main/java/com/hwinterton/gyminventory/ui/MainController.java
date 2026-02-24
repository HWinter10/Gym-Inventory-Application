package com.hwinterton.gyminventory.ui;

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

    @FXML
    private void onLogout() {
        Router.showLogin();
    }

    @FXML
    private void onManageProducts() {
        System.out.println("Manage Products clicked by " + currentUser.getRole());
    }
}
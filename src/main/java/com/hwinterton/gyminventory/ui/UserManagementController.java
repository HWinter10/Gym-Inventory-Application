package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UserManagementController {

    @FXML private TextField usernameField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.MANAGER, Role.STAFF));
    }

    @FXML
    private void onCreateUser() {
        try {
            Role role = roleCombo.getValue();
            if (role == null) {
                messageLabel.setText("Select a role.");
                return;
            }

            String tempPassword = userService.createUserWithTempPassword(usernameField.getText(), role);

            messageLabel.setText("User created. Temporary password: " + tempPassword + "  The user must change it at first login.");
            usernameField.clear();
            roleCombo.getSelectionModel().clearSelection();

        } catch (Exception ex) {
            messageLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Router.showMain(com.hwinterton.gyminventory.security.SessionManager.getUser());
    }
}
package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void onUpdatePassword() {
        try {
            String current = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirm = confirmPasswordField.getText();

            if (newPass == null || newPass.length() < 8) {
                messageLabel.setText("New password must be at least 8 characters.");
                return;
            }
            if (!newPass.equals(confirm)) {
                messageLabel.setText("Passwords do not match.");
                return;
            }

            userService.changeOwnPassword(current, newPass);
            messageLabel.setText("Password updated.");

            Router.showMain(com.hwinterton.gyminventory.security.SessionManager.getUser());
        } catch (Exception ex) {
            messageLabel.setText(ex.getMessage());
        }
    }
}
/*
 * Purpose:
 * - forces user to change password when mustChangePassword is true
 * 
 * Function:
 * - reads current password and new password from form
 * - calls password change
 * - on success, routes back to Main
 * 
 * Dependencies:
 * - UserService for password change logic
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField; // field for current password
    @FXML private PasswordField newPasswordField; // field for new password
    @FXML private PasswordField confirmPasswordField; // field for confirming new password
    @FXML private Label messageLabel; // displays validation or success messages

    private final UserService userService = new UserService(); // password change business logic

    // Method - handle password update action from UI
    @FXML
    private void onUpdatePassword() {
        try {
            hideMessage();

            String current = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirm = confirmPasswordField.getText();

            if (newPass == null || newPass.length() < 8) {
                showError("New password must be at least 8 characters long.");
                return;
            }

            if (!newPass.equals(confirm)) {
                showError("New password and confirmation do not match.");
                return;
            }

            userService.changeOwnPassword(current, newPass);

            showSuccess("Password updated successfully.");
            Router.showMain();

        } catch (Exception ex) {
            showError(ex.getMessage());
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
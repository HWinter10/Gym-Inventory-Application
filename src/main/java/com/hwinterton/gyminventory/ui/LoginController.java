/*
 * Purpose:
 * - handles user Login UI events
 * 
 * Function:
 * - reads username & password from login form
 * - calls AuthenticationService
 * - if successful, stores logged in user in SessionManager
 * - routes to Change Password if mustChangePassword, otherwise routes to Main
 * 
 * Dependencies:
 * - AuthenticationService for credential verification
 * - SessionManager for storing logged in user
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.security.SessionManager;
import com.hwinterton.gyminventory.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField; // login username input
    @FXML private PasswordField passwordField; // login password input
    @FXML private Label messageLabel; // displays login errors

    private final AuthenticationService authService = new AuthenticationService(); // authentication logic

    // Method - handle login button action
    @FXML
    private void onLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        hideMessage();

        authService.login(username, password).ifPresentOrElse(
                user -> {
                    SessionManager.setUser(user);

                    if (user.mustChangePassword()) {
                        Router.showChangePassword();
                    } else {
                        Router.showMain();
                    }
                },
                () -> showError("Sign-in failed. Check your username and password and try again.")
        );
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
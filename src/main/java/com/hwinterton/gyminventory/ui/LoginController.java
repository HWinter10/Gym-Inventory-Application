/*
 * Purpose:
 * - handles user Login UI events
 * 
 * Function:
 * - reads username & password from login form
 * - calls AuthenticationService.Login
 * - if successful, stores logged in user in SessionManager
 * routes to Change Password if mustChangePassword, otherwise routes to Main
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

        // read credentials from login form
        String username = usernameField.getText();
        String password = passwordField.getText();

        // attempt authentication and handle result
        authService.login(username, password).ifPresentOrElse(

                // successful login
                user -> {
                    // store authenticated user in session
                    SessionManager.setUser(user);

                    // route to password change if required
                    if (user.mustChangePassword()) {
                        Router.showChangePassword();
                    } else {
                        Router.showMain(user);
                    }
                },

                // authentication failed
                () -> messageLabel.setText("Invalid username or password.")
        );
    }
}
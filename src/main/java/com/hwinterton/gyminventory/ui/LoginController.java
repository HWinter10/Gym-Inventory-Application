package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.security.SessionManager;
import com.hwinterton.gyminventory.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        authService.login(username, password).ifPresentOrElse(
                user -> {
                    SessionManager.setUser(user);
                    Router.showMain(user);
                },
                () -> messageLabel.setText("Invalid username or password.")
        );
    }
}
/*
 * Purpose:
 * - owner only UI for managing user accounts
 *
 * Function:
 * - loads and displays list of users in TableView
 * - creates new users with owner chosen temporary passwords
 * - updates selected user username and role
 * - updates active status for selected user
 * - resets passwords and forces change at next login
 *
 * Dependencies:
 * - UserService for user administration
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.UserSummary;
import com.hwinterton.gyminventory.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class UserManagementController {

    @FXML private TextField newUsernameField; // new username input
    @FXML private ComboBox<Role> newRoleCombo; // new user role selection

    @FXML private TableView<UserSummary> userTable; // user table
    @FXML private TableColumn<UserSummary, Long> colId; // user id column
    @FXML private TableColumn<UserSummary, String> colUsername; // username column
    @FXML private TableColumn<UserSummary, Role> colRole; // role column
    @FXML private TableColumn<UserSummary, Boolean> colActive; // active status column

    @FXML private TextField editUsernameField; // selected user username input
    @FXML private ComboBox<Role> editRoleCombo; // selected user role input
    @FXML private CheckBox activeCheckBox; // selected user active flag
    @FXML private Label messageLabel; // status and validation message

    private final UserService userService = new UserService(); // user administration logic
    private final ObservableList<UserSummary> userRows = FXCollections.observableArrayList(); // table backing list

    // Method - initialize user management screen
    @FXML
    private void initialize() {
        hideMessage();

        newRoleCombo.setItems(FXCollections.observableArrayList(Role.MANAGER, Role.STAFF));
        editRoleCombo.setItems(FXCollections.observableArrayList(Role.MANAGER, Role.STAFF));

        userTable.setItems(userRows);

        userTable.getColumns().clear();
        userTable.getColumns().addAll(colId, colUsername, colRole, colActive);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editUsernameField.setText(newSelection.getUsername());
                editRoleCombo.setValue(newSelection.getRole());
                activeCheckBox.setSelected(newSelection.isActive());
            } else {
                editUsernameField.clear();
                editRoleCombo.getSelectionModel().clearSelection();
                activeCheckBox.setSelected(false);
            }
        });

        refresh();
    }

    // Method - create new user from form input
    @FXML
    private void onCreateUser() {
        Role role = newRoleCombo.getValue();
        if (role == null) {
            showError("Please select a role for the new user.");
            return;
        }

        String username = newUsernameField.getText();
        if (username == null || username.isBlank()) {
            showError("Please enter a username.");
            return;
        }

        try {
            String initialPassword = promptForPassword("Create User", "Set a temporary password for: " + username);
            if (initialPassword == null) {
                return;
            }

            userService.createUserWithInitialPassword(username, role, initialPassword);

            showSuccess("User created successfully. Password change required at first sign-in.");
            newUsernameField.clear();
            newRoleCombo.getSelectionModel().clearSelection();
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - update selected user from form input
    @FXML
    private void onUpdateUser() {
        UserSummary selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user.");
            return;
        }

        Role newRole = editRoleCombo.getValue();
        if (newRole == null) {
            showError("Please select a role.");
            return;
        }

        try {
            userService.updateUser(selected.getId(), editUsernameField.getText(), newRole);
            showSuccess("User updated successfully.");
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - toggle active status for selected user
    @FXML
    private void onToggleActive() {
        UserSummary selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a user first.");
            activeCheckBox.setSelected(!activeCheckBox.isSelected());
            return;
        }

        try {
            userService.setActive(selected.getId(), activeCheckBox.isSelected());
            showSuccess("User status updated successfully.");
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
            activeCheckBox.setSelected(!activeCheckBox.isSelected());
        }
    }

    // Method - reset password for selected user
    @FXML
    private void onResetPassword() {
        UserSummary selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user.");
            return;
        }

        try {
            String password = promptForPassword("Reset Password", "Set a temporary password for: " + selected.getUsername());
            if (password == null) {
                return;
            }

            userService.resetPasswordTo(selected.getId(), password);
            showSuccess("Password reset successfully. Password change required at next sign-in.");
            refresh();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - show password dialog with confirmation
    private String promptForPassword(String title, String header) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (8+ characters, no spaces)");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");

        VBox box = new VBox(8, passwordField, confirmField);
        dialog.getDialogPane().setContent(box);

        Node okButton = dialog.getDialogPane().lookupButton(okType);
        okButton.setDisable(true);

        Runnable validate = () -> {
            String first = passwordField.getText() == null ? "" : passwordField.getText();
            String second = confirmField.getText() == null ? "" : confirmField.getText();
            boolean longEnough = first.length() >= 8;
            boolean matches = first.equals(second);
            boolean noSpaces = !first.contains(" ");
            okButton.setDisable(!(longEnough && matches && noSpaces));
        };

        passwordField.textProperty().addListener((obs, oldValue, newValue) -> validate.run());
        confirmField.textProperty().addListener((obs, oldValue, newValue) -> validate.run());

        dialog.setResultConverter(button -> button == okType ? passwordField.getText().trim() : null);

        return dialog.showAndWait().orElse(null);
    }

    // Method - reload user table from database
    @FXML
    private void onRefresh() {
        try {
            refresh();
            showSuccess("User list refreshed.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Method - refresh user rows
    private void refresh() {
        userRows.setAll(userService.listUsers());
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }

    // Method - show success message
    private void showSuccess(String text) {
        showMessage(text, "message-success");
    }

    // Method - show error message
    private void showError(String text) {
        showMessage(text, "message-error");
    }

    // Method - apply message style and text
    private void showMessage(String text, String styleClass) {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setText(text);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    // Method - clear and hide message
    private void hideMessage() {
        messageLabel.getStyleClass().removeAll("message-success", "message-error");
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
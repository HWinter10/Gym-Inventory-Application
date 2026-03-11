/*
 * Purpose:
 * - owner only UI for managing user accounts
 * 
 * Function:
 * - load and display list of users in TableView
 * - creates new users with owner-chosen initial passwords
 * - changes user role
 * - enable or disable accounts instead of deleting them for better logging
 * - resets passwords using pop up, which also forces user to create new password on next login
 * 
 * Dependencies:
 * - UserService for all user admin operations
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class UserManagementController {

    @FXML private TextField newUsernameField; // input for new username
    @FXML private ComboBox<Role> newRoleCombo; // role selection for new user

    @FXML private TableView<UserSummary> userTable; // displays user accounts
    @FXML private TableColumn<UserSummary, Long> colId; // user id column
    @FXML private TableColumn<UserSummary, String> colUsername; // username column
    @FXML private TableColumn<UserSummary, Role> colRole; // role column
    @FXML private TableColumn<UserSummary, Boolean> colActive; // active status column

    @FXML private ComboBox<Role> editRoleCombo; // role selection for existing user
    @FXML private Label messageLabel; // displays status and validation messages

    private final UserService userService = new UserService(); // user administration logic
    private final ObservableList<UserSummary> userRows = FXCollections.observableArrayList(); // table backing list

    // Method - initialize user management screen
    @FXML
    private void initialize() {

        // limit UI role options to non owner roles
        newRoleCombo.setItems(FXCollections.observableArrayList(Role.MANAGER, Role.STAFF));
        editRoleCombo.setItems(FXCollections.observableArrayList(Role.MANAGER, Role.STAFF));

        // bind observable list to table
        userTable.setItems(userRows);

        // rebuild table columns in expected order
        userTable.getColumns().clear();
        userTable.getColumns().addAll(colId, colUsername, colRole, colActive);

        // map table columns to UserSummary properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        // stretch columns to fit table width
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refresh();
    }

    // Method - create new user from form input
    @FXML
    private void onCreateUser() {

        // read selected role for new user
        var role = newRoleCombo.getValue();
        if (role == null) {
            messageLabel.setText("Select a role for the new user.");
            return;
        }

        // read username from form
        String uname = newUsernameField.getText();
        if (uname == null || uname.isBlank()) {
            messageLabel.setText("Enter a username.");
            return;
        }

        try {
            // prompt owner to set initial password
            String initialPassword = promptForPassword("Create User", "Set initial password for: " + uname);
            if (initialPassword == null) return;

            userService.createUserWithInitialPassword(uname, role, initialPassword);

            messageLabel.setText("User created. They must change password at first login.");

            // clear form after successful creation
            newUsernameField.clear();
            newRoleCombo.getSelectionModel().clearSelection();

            refresh();

        } catch (Exception ex) { // display service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - apply selected role to selected user
    @FXML
    private void onApplyRole() {

        // require selected table row
        var selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user first.");
            return;
        }

        // require selected replacement role
        Role newRole = editRoleCombo.getValue();
        if (newRole == null) {
            messageLabel.setText("Select a role to apply.");
            return;
        }

        try {
            userService.changeRole(selected.getId(), newRole);
            messageLabel.setText("Role updated.");
            refresh();

        } catch (Exception ex) { // display service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - disable selected user account
    @FXML
    private void onDisable() {
        setActive(false);
    }

    // Method - enable selected user account
    @FXML
    private void onEnable() {
        setActive(true);
    }

    // Method - update selected user active status
    private void setActive(boolean active) {

        // require selected table row
        var selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user first.");
            return;
        }

        try {
            userService.setActive(selected.getId(), active);

            // show result based on chosen action
            messageLabel.setText(active ? "User enabled." : "User disabled.");

            refresh();

        } catch (Exception ex) { // display service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - reset password for selected user
    @FXML
    private void onResetPassword() {

        // require selected table row
        var selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user first.");
            return;
        }

        try {
            // prompt owner for temporary password
            String pw = promptForPassword("Reset Password", "Set temporary password for: " + selected.getUsername());
            if (pw == null) return;

            userService.resetPasswordTo(selected.getId(), pw);

            messageLabel.setText("Password reset. User must change password at login.");

            refresh();

        } catch (Exception ex) { // display service or validation error
            messageLabel.setText(ex.getMessage());
        }
    }

    // Method - show password dialog with confirmation and validation
    private String promptForPassword(String title, String header) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // define OK button for dialog result handling
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        // first password entry
        PasswordField pw1 = new PasswordField();
        pw1.setPromptText("Password (8+ characters, no spaces)");

        // confirmation entry
        PasswordField pw2 = new PasswordField();
        pw2.setPromptText("Confirm password");

        // stack password fields vertically
        VBox box = new VBox(8, pw1, pw2);
        dialog.getDialogPane().setContent(box);

        // disable OK until password rules pass
        Node okButton = dialog.getDialogPane().lookupButton(okType);
        okButton.setDisable(true);

        // validate length, match, and no spaces
        Runnable validate = () -> {
            String a = pw1.getText() == null ? "" : pw1.getText();
            String b = pw2.getText() == null ? "" : pw2.getText();
            boolean longEnough = a.length() >= 8;
            boolean matches = a.equals(b);
            boolean noSpaces = !a.contains(" ");
            okButton.setDisable(!(longEnough && matches && noSpaces));
        };

        // revalidate on each text change
        pw1.textProperty().addListener((obs, o, n) -> validate.run());
        pw2.textProperty().addListener((obs, o, n) -> validate.run());

        // return trimmed password only when OK selected
        dialog.setResultConverter(btn -> btn == okType ? pw1.getText().trim() : null);

        return dialog.showAndWait().orElse(null);
    }

    // Method - reload user table from database
    @FXML
    private void onRefresh() {
        refresh();
    }

    // Method - refresh user rows for table view
    private void refresh() {
        userRows.setAll(userService.listUsers());
    }

    // Method - return to main menu
    @FXML
    private void onBack() {
        Router.showMain();
    }
}
/*
 * Purpose:
 * - controls first run setup screen
 *
 * Function:
 * - displays generated temporary credentials for seeded owner accounts
 * - shows username and password for each first run account
 * - routes user to login screen after acknowledging credentials
 *
 * Dependencies:
 * - StartupContext for generated first run credentials
 * - Router for navigation
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.startup.StartupContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FirstRunSetupController {

    @FXML private Label ownerLabel; // displays owner username and temporary password
    @FXML private Label backupLabel; // displays backup owner username and temporary password

    // Method - initialize first run setup screen with generated credentials
    @FXML
    private void initialize() {
        ownerLabel.setText("Username: owner | Temporary password: " + StartupContext.getOwnerPassword());
        backupLabel.setText("Username: owner_backup | Temporary password: " + StartupContext.getBackupOwnerPassword());
    }

    // Method - continue from first run screen to login
    @FXML
    private void onContinue() {
        StartupContext.clear();
        Router.showLogin();
    }
}
package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.FirstRunCredentials;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FirstRunSetupController {

    @FXML private Label ownerLabel;
    @FXML private Label backupLabel;

    public void setCredentials(FirstRunCredentials creds) {
        ownerLabel.setText(creds.ownerUsername() + " : " + creds.ownerPassword());
        backupLabel.setText(creds.backupUsername() + " : " + creds.backupPassword());
    }

    @FXML
    private void onContinue() {
        Router.showLogin();
    }
}
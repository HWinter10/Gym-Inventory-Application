package com.hwinterton.gyminventory;

import com.hwinterton.gyminventory.ui.Router;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        var creds = com.hwinterton.gyminventory.data.SchemaInitializer.initialize();
        Router.init(stage);

        if (creds != null) {
            Router.showFirstRunSetup(creds);
        } else {
            Router.showLogin();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
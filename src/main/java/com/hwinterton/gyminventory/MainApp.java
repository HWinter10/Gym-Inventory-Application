/*
 * Purpose:
 * - starts application
 * 
 * Function:
 * - ensures database schema exists by calling SchemaInitializer
 * - shows first run setup screen when initial accounts are created
 * - otherwise loads normal login screen
 * 
 * Dependencies:
 * - SchemaInitializer for database setup
 * - StartupContext for first run detection
 * - Router for screen navigation
 */

package com.hwinterton.gyminventory;

import com.hwinterton.gyminventory.data.SchemaInitializer;
import com.hwinterton.gyminventory.startup.StartupContext;
import com.hwinterton.gyminventory.ui.Router;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    // Method - JavaFX entry point after launch()
    @Override
    public void start(Stage stage) {

        // ensure database tables and initial users exist
        SchemaInitializer.initialize();

        // initialize router with primary application stage
        Router.init(stage);

        // show first run credential screen only when initial users were just created
        if (StartupContext.isFirstRun()) {
            Router.showFirstRunSetup();
        } else {
            Router.showLogin();
        }
    }

    // Method - program entry point
    public static void main(String[] args) {
        launch(args);
    }
}
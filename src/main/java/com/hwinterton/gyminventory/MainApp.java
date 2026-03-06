/*
 * Purpose:
 * - starts application
 * 
 * Function:
 * - ensures database schema exists by calling SchemaInitializer
 * - loads first screen (login screen) using Router
 * 
 * Dependencies:
 * - SchemaInitializer for database setup
 * - Router for screen navigation
 */

package com.hwinterton.gyminventory;

import com.hwinterton.gyminventory.data.SchemaInitializer;
import com.hwinterton.gyminventory.ui.Router;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    // Method - JavaFX entry point after launch()
    @Override
    public void start(Stage stage) {

        // ensure database tables and seed accounts exist
        SchemaInitializer.initialize();

        // initialize router with primary application stage
        Router.init(stage);

        // load first screen (login UI)
        Router.showLogin();
    }

    // Method - program entry point
    public static void main(String[] args) {
        launch(args); // start JavaFX application lifecycle
    }
}
/*
 * Purpose: 
 * - switches JavaFX screens
 * 
 * Function:
 * - loads FXML file with FXMLLoader
 * - sets Scene on primary Stage
 * - provides helper methods like showLogin, showMain, showUserManagement, showProductManagement, showSalesEntry, showInventoryAdjustment, showReorderAlerts, and showFirstRunSetup
 * 
 * Dependencies:
 * - FXML resources
 * - JavaFX Stage and Scene
 */

package com.hwinterton.gyminventory.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Router {

    private static Stage primaryStage; // main application window

    private Router() {}

    // Method - initialize router with primary stage
    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gym Inventory");
    }

    // Method - route to first run setup screen
    public static void showFirstRunSetup() {
        setScene("/com/hwinterton/gyminventory/ui/views/first_run_setup.fxml", 620, 320);
    }

    // Method - route to login screen
    public static void showLogin() {
        setScene("/com/hwinterton/gyminventory/ui/views/login.fxml", 520, 360);
    }

    // Method - route to main menu
    public static void showMain() {
        setScene("/com/hwinterton/gyminventory/ui/views/main.fxml", 760, 600);
    }

    // Method - route to user management screen
    public static void showUserManagement() {
        setScene("/com/hwinterton/gyminventory/ui/views/user_management.fxml", 760, 520);
    }

    // Method - route to product management screen
    public static void showProductManagement() {
        setScene("/com/hwinterton/gyminventory/ui/views/product_management.fxml", 900, 600);
    }

    // Method - route to sales entry screen
    public static void showSalesEntry() {
        setScene("/com/hwinterton/gyminventory/ui/views/sales_entry.fxml", 700, 420);
    }

    // Method - route to inventory adjustment screen
    public static void showInventoryAdjustment() {
        setScene("/com/hwinterton/gyminventory/ui/views/inventory_adjustment.fxml", 760, 520);
    }

    // Method - route to reorder alerts screen
    public static void showReorderAlerts() {
        setScene("/com/hwinterton/gyminventory/ui/views/reorder_alerts.fxml", 980, 560);
    }

    // Method - route to forced password change screen
    public static void showChangePassword() {
        setScene("/com/hwinterton/gyminventory/ui/views/change_password.fxml", 520, 360);
    }

    // Method - load FXML and apply new scene to primary stage
    private static void setScene(String fxmlPath, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }
}
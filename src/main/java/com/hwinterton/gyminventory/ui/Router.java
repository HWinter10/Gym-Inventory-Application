/*
 * Purpose:
 * - switches JavaFX screens
 *
 * Function:
 * - loads FXML file with FXMLLoader
 * - sets Scene on primary Stage
 * - protects restricted screens by checking session and role before loading
 *
 * Dependencies:
 * - FXML resources
 * - JavaFX Stage and Scene
 * - SessionManager for session checks
 */

package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class Router {

    private static Stage primaryStage; // main application window

    private Router() {}

    // Method - initialize router with primary stage
    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Inventory Manager");
    }

    // Method - route to first run setup screen
    public static void showFirstRunSetup() {
        setScene("/com/hwinterton/gyminventory/ui/views/first_run_setup.fxml", 900, 650);
    }

    // Method - route to login screen
    public static void showLogin() {
        setScene("/com/hwinterton/gyminventory/ui/views/login.fxml", 560, 360);
    }

    // Method - route to main menu
    public static void showMain() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/main.fxml", 900, 650);
    }

    // Method - route to user management screen
    public static void showUserManagement() {
        User user = SessionManager.getUser();

        if (user == null) {
            showLogin();
            return;
        }

        if (user.getRole() != Role.OWNER) {
            showMain();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/user_management.fxml", 900, 650);
    }

    // Method - route to product management screen
    public static void showProductManagement() {
        User user = SessionManager.getUser();

        if (user == null) {
            showLogin();
            return;
        }

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            showMain();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/product_management.fxml", 900, 650);
    }

    // Method - route to sales entry screen
    public static void showSalesEntry() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/sales_entry.fxml", 900, 650);
    }

    // Method - route to inventory adjustment screen
    public static void showInventoryAdjustment() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/inventory_adjustment.fxml", 900, 650);
    }

    // Method - route to reorder alerts screen
    public static void showReorderAlerts() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/reorder_alerts.fxml", 900, 650);
    }

    // Method - route to forced password change screen
    public static void showChangePassword() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/change_password.fxml", 560, 400);
    }

    // Method - load FXML and apply new scene to primary stage
    private static void setScene(String fxmlPath, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            URL cssUrl = Router.class.getResource("/com/hwinterton/gyminventory/ui/styles/app.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }
}
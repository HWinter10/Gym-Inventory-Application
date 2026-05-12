package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.Role;
import com.hwinterton.gyminventory.domain.User;
import com.hwinterton.gyminventory.security.SessionManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Handles navigation between JavaFX screens.
 *
 *<p>This router loads FXML files, applies them to the primary stage and protects
 *restricted screens (RBAC) by checking current session and user role</p>
 *
 *<p>Uses {@link SessionManager} for session checks and JavaFX {@link Stage} and 
 *{@link Scene} for screen changes</p>
 */
public final class Router {

    private static Stage primaryStage; // main application window

    private Router() {}

    /**
     * Initializes the router with the application primary stage
     * 
     * @param stage the main JavaFX stage for application
     */
    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Inventory Manager");
    }

    /**
     * Routes to the first run setup screen
     */
    public static void showFirstRunSetup() {
        setScene("/com/hwinterton/gyminventory/ui/views/first_run_setup.fxml", 900, 650);
    }

    /**
     * Routes to the login screen
     */
    public static void showLogin() {
        setScene("/com/hwinterton/gyminventory/ui/views/login.fxml", 560, 360);
    }

    /**
     * Routes to the main menu
     */
    public static void showMain() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/main.fxml", 900, 650);
    }

    /**
     * Routes user to user management screen (owner only)
     */
    public static void showUserManagement() {
        User user = SessionManager.getUser();

        if (user == null) {
            showLogin();
            return;
        }
        // restrict to owner role
        if (user.getRole() != Role.OWNER) {
            showMain();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/user_management.fxml", 900, 650);
    }

    /**
     * Routes user to action log viewer (owner only)
     */
    public static void showActionLog() {
    	User user = SessionManager.getUser();
    	
    	if (user == null) {
    		showLogin();
    		return;
    	}
    	
    	// restrict to owner role
    	if (user.getRole() != Role.OWNER) {
    		showMain();
    		return;
    	}
    	
    	setScene("/com/hwinterton/gyminventory/ui/views/action_log.fxml", 900, 650);
    }
    
    /**
     * Routes user to product management screen (owner and manager only)
     */
    public static void showProductManagement() {
        User user = SessionManager.getUser();

        if (user == null) {
            showLogin();
            return;
        }
        // restrict to owner and manager roles
        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            showMain();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/product_management.fxml", 900, 650);
    }

    /**
     * Routes to sales entry screen
     */
    public static void showSalesEntry() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/sales_entry.fxml", 900, 650);
    }

    /**
     * Routes to inventory adjustment screen
     */
    public static void showInventoryAdjustment() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/inventory_adjustment.fxml", 900, 650);
    }

    /**
     * Routes to reorder alert screen
     */
    public static void showReorderAlerts() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/reorder_alerts.fxml", 900, 650);
    }

    /**
     * Routes to forced password change pop up
     */
    public static void showChangePassword() {
        if (!SessionManager.isLoggedIn()) {
            showLogin();
            return;
        }

        setScene("/com/hwinterton/gyminventory/ui/views/change_password.fxml", 560, 400);
    }

    /**
     * Loads FXML file and applies to primary stage
     * 
     * @param fxmlPath class path location for FXML file
     * @param width screen width
     * @param height screen height
     */
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
package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.FirstRunCredentials;
import com.hwinterton.gyminventory.domain.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public final class Router {

    private static Stage primaryStage;

    private Router() {}

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gym Inventory");
    }

    public static void showLogin() {
        setScene("/com/hwinterton/gyminventory/ui/views/login.fxml", 400, 300);
    }

    public static void showMain(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Router.class.getResource("/com/hwinterton/gyminventory/ui/views/main.fxml")
            );
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setUser(user);

            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load main screen", e);
        }
    }

    public static void showUserManagement() {
        setScene("/com/hwinterton/gyminventory/ui/views/user_management.fxml", 560, 340);
    }

    public static void showChangePassword() {
        setScene("/com/hwinterton/gyminventory/ui/views/change_password.fxml", 520, 320);
    }

    public static void showFirstRunSetup(FirstRunCredentials creds) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Router.class.getResource("/com/hwinterton/gyminventory/ui/views/first_run_setup.fxml")
            );
            Parent root = loader.load();
            FirstRunSetupController controller = loader.getController();
            controller.setCredentials(creds);

            primaryStage.setScene(new Scene(root, 620, 320));
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load first run setup screen", e);
        }
    }

    private static void setScene(String fxmlPath, int width, int height) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    Router.class.getResource(fxmlPath),
                    "Missing FXML: " + fxmlPath
            ));
            primaryStage.setScene(new Scene(root, width, height));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }
}
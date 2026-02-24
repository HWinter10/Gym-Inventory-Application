package com.hwinterton.gyminventory.ui;

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
        setScene("/com/hwinterton/gyminventory/ui/views/login.fxml");
    }

    public static void showMain(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Router.class.getResource(
                            "/com/hwinterton/gyminventory/ui/views/main.fxml"
                    )
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

    private static void setScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            Router.class.getResource(fxmlPath)
                    )
            );

            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene", e);
        }
    }
}
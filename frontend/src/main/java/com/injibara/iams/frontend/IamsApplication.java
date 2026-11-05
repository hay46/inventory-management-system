package com.injibara.iams.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IamsApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Inventory and Asset Management System (IAMS)");
        showLogin();
        stage.show();
    }

    public static void showLogin() throws Exception {
        Parent root = FXMLLoader.load(IamsApplication.class.getResource("/fxml/login.fxml"));
        primaryStage.setScene(new Scene(root, 420, 320));
    }

    public static void showMain() throws Exception {
        Parent root = FXMLLoader.load(IamsApplication.class.getResource("/fxml/main.fxml"));
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

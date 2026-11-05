package com.injibara.iams.frontend.controller;

import com.injibara.iams.frontend.IamsApplication;
import com.injibara.iams.frontend.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private StackPane contentPane;
    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        UserSession s = UserSession.getInstance();
        userLabel.setText(s.getFullName() + " (" + s.getRole() + ")");
        showDashboard();
    }

    private void load(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void showDashboard() { load("dashboard.fxml"); }
    @FXML public void showInventory() { load("inventory.fxml"); }
    @FXML public void showReceive() { load("receive.fxml"); }
    @FXML public void showIssue() { load("issue.fxml"); }
    @FXML public void showLowStock() { load("lowstock.fxml"); }
    @FXML public void showTransactions() { load("transactions.fxml"); }

    @FXML
    private void handleLogout() throws Exception {
        UserSession.getInstance().clear();
        IamsApplication.showLogin();
    }
}

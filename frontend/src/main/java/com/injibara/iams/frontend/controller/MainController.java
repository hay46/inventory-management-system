package com.injibara.iams.frontend.controller;

import com.injibara.iams.frontend.IamsApplication;
import com.injibara.iams.frontend.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.awt.Desktop;
import java.net.URI;

public class MainController {

    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        InfoController.setMain(this);
        SignupController.setMain(this);
        showHome();
    }

    private void load(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void showHome() { load("home.fxml"); }
    @FXML public void showAbout() { load("about.fxml"); }
    @FXML public void showGallery() { load("gallery.fxml"); }
    @FXML public void showServices() { load("services.fxml"); }
    @FXML public void showContact() { load("contact.fxml"); }
    @FXML public void showSignIn() { load("login.fxml"); }
    @FXML public void showSignUp() { load("signup.fxml"); }

    @FXML
    public void openWhatsApp() {
        try {
            Desktop.getDesktop().browse(new URI("https://wa.me/251900000000?text=Hello%20IAMS%20Support"));
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("WhatsApp");
            a.setHeaderText(null);
            a.setContentText("Open WhatsApp: https://wa.me/251900000000");
            a.showAndWait();
        }
    }

    // Keep previous app pages accessible after login
    @FXML public void showDashboard() { load("dashboard.fxml"); }
    @FXML public void showInventory() { load("inventory.fxml"); }
    @FXML public void showReceive() { load("receive.fxml"); }
    @FXML public void showIssue() { load("issue.fxml"); }
    @FXML public void showLowStock() { load("lowstock.fxml"); }
    @FXML public void showTransactions() { load("transactions.fxml"); }
    @FXML public void showReports() { load("reports.fxml"); }
}

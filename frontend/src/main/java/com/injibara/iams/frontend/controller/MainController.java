package com.injibara.iams.frontend.controller;

import com.injibara.iams.frontend.IamsApplication;
import com.injibara.iams.frontend.model.UserSession;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.awt.Desktop;
import java.net.URI;

public class MainController {

    @FXML private StackPane contentPane;
    private boolean darkMode = false;

    @FXML
    public void initialize() {
        InfoController.setMain(this);
        SignupController.setMain(this);
        showHome();
    }

    private boolean requireLogin() {
        if (!UserSession.getInstance().isLoggedIn()) {
            showSignIn();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Authentication required");
            a.setHeaderText(null);
            a.setContentText("Please sign in to access this page.");
            a.showAndWait();
            return false;
        }
        return true;
    }

    private void load(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            contentPane.getChildren().setAll(view);
            FadeTransition ft = new FadeTransition(Duration.millis(250), view);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void toggleDarkMode() {
        darkMode = !darkMode;
        if (contentPane.getScene() != null && contentPane.getScene().getRoot() != null) {
            if (darkMode) contentPane.getScene().getRoot().getStyleClass().add("dark");
            else contentPane.getScene().getRoot().getStyleClass().remove("dark");
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
    @FXML public void showDashboard() { if (requireLogin()) load("dashboard.fxml"); }
    @FXML public void showInventory() { if (requireLogin()) load("inventory.fxml"); }
    @FXML public void showReceive() { if (requireLogin()) load("receive.fxml"); }
    @FXML public void showIssue() { if (requireLogin()) load("issue.fxml"); }
    @FXML public void showLowStock() { if (requireLogin()) load("lowstock.fxml"); }
    @FXML public void showTransactions() { if (requireLogin()) load("transactions.fxml"); }
    @FXML public void showReports() { if (requireLogin()) load("reports.fxml"); }
}

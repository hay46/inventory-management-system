package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.IamsApplication;
import com.injibara.iams.frontend.model.UserSession;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleLogin() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Username and password are required");
            return;
        }
        statusLabel.setText("Logging in...");
        Thread t = new Thread(() -> {
            try {
                var body = new java.util.HashMap<String, String>();
                body.put("username", u);
                body.put("password", p);
                var resp = ApiClient.post("/api/auth/login", body);
                if (resp.statusCode() == 200) {
                    JsonNode node = ApiClient.mapper().readTree(resp.body());
                    UserSession.getInstance().set(
                            node.get("token").asText(),
                            node.get("username").asText(),
                            node.get("fullName").asText(),
                            node.get("role").asText());
                    Platform.runLater(() -> {
                        try { IamsApplication.showMain(); }
                        catch (Exception e) { statusLabel.setText("Failed to open main window"); }
                    });
                } else {
                    JsonNode node = ApiClient.mapper().readTree(resp.body());
                    String msg = node.has("message") ? node.get("message").asText() : "Login failed";
                    Platform.runLater(() -> statusLabel.setText(msg));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Server unreachable: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }
}

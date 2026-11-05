package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.model.UserSession;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class SignupController {

    private static MainController main;
    public static void setMain(MainController m) { main = m; }

    @FXML private TextField usernameField, fullNameField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        Thread t = new Thread(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("username", usernameField.getText().trim());
                body.put("password", passwordField.getText());
                body.put("fullName", fullNameField.getText().trim());
                body.put("email", emailField.getText().trim());
                var resp = ApiClient.post("/api/auth/register", body);
                Platform.runLater(() -> {
                    if (resp.statusCode() == 200) {
                        try {
                            JsonNode node = ApiClient.mapper().readTree(resp.body());
                            UserSession.getInstance().set(node.get("token").asText(), node.get("username").asText(),
                                    node.get("fullName").asText(), node.get("role").asText());
                            statusLabel.setText("Registered! Welcome " + node.get("username").asText());
                            if (main != null) main.showDashboard();
                        } catch (Exception e) { statusLabel.setText("Registered but parse error"); }
                    } else {
                        try {
                            JsonNode node = ApiClient.mapper().readTree(resp.body());
                            statusLabel.setText(node.has("message") ? node.get("message").asText() : resp.body());
                        } catch (Exception e) { statusLabel.setText(resp.body()); }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        });
        t.setDaemon(true); t.start();
    }

    public void goHome() { if (main != null) main.showHome(); }
}

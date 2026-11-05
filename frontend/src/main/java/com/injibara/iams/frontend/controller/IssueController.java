package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class IssueController {
    @FXML private TextField itemIdField, qtyField, refField, notesField;
    @FXML private Label statusLabel;

    @FXML
    private void handleIssue() {
        Thread t = new Thread(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("itemId", Long.parseLong(itemIdField.getText().trim()));
                body.put("quantity", Integer.parseInt(qtyField.getText().trim()));
                body.put("referenceNumber", refField.getText().trim());
                body.put("notes", notesField.getText().trim());
                var resp = ApiClient.post("/api/stock/issue", body);
                Platform.runLater(() -> {
                    if (resp.statusCode() == 200) {
                        statusLabel.setText("Issued successfully");
                        itemIdField.clear(); qtyField.clear(); refField.clear(); notesField.clear();
                    } else {
                        try {
                            JsonNode n = ApiClient.mapper().readTree(resp.body());
                            statusLabel.setText(n.has("message") ? n.get("message").asText() : resp.body());
                        } catch (Exception e) { statusLabel.setText(resp.body()); }
                    }
                });
            } catch (NumberFormatException e) {
                Platform.runLater(() -> statusLabel.setText("Invalid number"));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        });
        t.setDaemon(true); t.start();
    }
}

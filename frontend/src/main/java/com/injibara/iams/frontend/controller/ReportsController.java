package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ReportsController {

    @FXML private TextArea summaryArea;

    private void downloadBinary(String path, String defaultName, String ext) {
        Thread t = new Thread(() -> {
            try {
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create("http://localhost:8080" + path))
                        .header("Authorization", "Bearer " + com.injibara.iams.frontend.model.UserSession.getInstance().getToken())
                        .GET().build();
                java.net.http.HttpResponse<byte[]> resp = java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofByteArray());
                if (resp.statusCode() == 200) {
                    byte[] data = resp.body();
                    Platform.runLater(() -> {
                        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
                        fc.setTitle("Save Report");
                        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter(ext.toUpperCase() + " Files", "*." + ext));
                        fc.setInitialFileName(defaultName);
                        java.io.File file = fc.showSaveDialog(summaryArea.getScene().getWindow());
                        if (file != null) {
                            try { java.nio.file.Files.write(file.toPath(), data); summaryArea.setText("Saved: " + file.getAbsolutePath()); }
                            catch (Exception e) { summaryArea.setText("Error: " + e.getMessage()); }
                        }
                    });
                } else {
                    Platform.runLater(() -> summaryArea.setText("Error: HTTP " + resp.statusCode()));
                }
            } catch (Exception e) { Platform.runLater(() -> summaryArea.setText("Error: " + e.getMessage())); }
        });
        t.setDaemon(true); t.start();
    }

    private void download(String path, String defaultName) {
        Thread t = new Thread(() -> {
            try {
                var resp = ApiClient.get(path);
                if (resp.statusCode() == 200) {
                    byte[] data = resp.body().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    Platform.runLater(() -> {
                        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
                        fc.setTitle("Save Report");
                        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                        fc.setInitialFileName(defaultName);
                        java.io.File file = fc.showSaveDialog(summaryArea.getScene().getWindow());
                        if (file != null) {
                            try { java.nio.file.Files.write(file.toPath(), data); summaryArea.setText("Saved: " + file.getAbsolutePath()); }
                            catch (Exception e) { summaryArea.setText("Error: " + e.getMessage()); }
                        }
                    });
                } else {
                    Platform.runLater(() -> summaryArea.setText("Error: " + resp.body()));
                }
            } catch (Exception e) { Platform.runLater(() -> summaryArea.setText("Error: " + e.getMessage())); }
        });
        t.setDaemon(true); t.start();
    }

    @FXML public void exportInventory() { download("/api/reports/inventory", "inventory_report.csv"); }
    @FXML public void exportInventoryPdf() { downloadBinary("/api/reports/inventory/pdf", "inventory_report.pdf", "pdf"); }
    @FXML public void exportLowStock() { download("/api/reports/low-stock", "low_stock_report.csv"); }
    @FXML public void exportLowStockPdf() { downloadBinary("/api/reports/low-stock/pdf", "low_stock_report.pdf", "pdf"); }
    @FXML public void exportTransactions() { download("/api/reports/transactions", "transaction_report.csv"); }

    @FXML
    public void showSummary() {
        Thread t = new Thread(() -> {
            try {
                var resp = ApiClient.get("/api/reports/summary");
                if (resp.statusCode() == 200) {
                    JsonNode n = ApiClient.mapper().readTree(resp.body());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Total Items: ").append(n.get("totalItems").asLong()).append("\n");
                    sb.append("Total Quantity: ").append(n.get("totalQuantity").asLong()).append("\n");
                    sb.append("Low Stock: ").append(n.get("lowStockCount").asLong()).append("\n");
                    sb.append("Out of Stock: ").append(n.get("outOfStockCount").asLong()).append("\n");
                    sb.append("Receive Transactions: ").append(n.get("totalReceiveTransactions").asLong()).append("\n");
                    sb.append("Issue Transactions: ").append(n.get("totalIssueTransactions").asLong()).append("\n");
                    Platform.runLater(() -> summaryArea.setText(sb.toString()));
                }
            } catch (Exception e) { Platform.runLater(() -> summaryArea.setText("Error: " + e.getMessage())); }
        });
        t.setDaemon(true); t.start();
    }
}

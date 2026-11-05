package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class DashboardController {

    @FXML private Label totalItemsLabel, totalQtyLabel, lowStockLabel, outOfStockLabel;
    @FXML private TableView<Map<String, Object>> recentTable;
    @FXML private TableColumn<Map<String, Object>, Object> colTxId, colTxItem, colTxType, colTxQty, colTxDate;

    @FXML
    public void initialize() { refresh(); }

    @FXML
    public void refresh() {
        Thread t = new Thread(() -> {
            try {
                var resp = ApiClient.get("/api/dashboard/summary");
                if (resp.statusCode() == 200) {
                    JsonNode node = ApiClient.mapper().readTree(resp.body());
                    Platform.runLater(() -> {
                        totalItemsLabel.setText(String.valueOf(node.get("totalItems").asLong()));
                        totalQtyLabel.setText(String.valueOf(node.get("totalQuantity").asLong()));
                        lowStockLabel.setText(String.valueOf(node.get("lowStockCount").asLong()));
                        outOfStockLabel.setText(String.valueOf(node.get("outOfStockCount").asLong()));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}

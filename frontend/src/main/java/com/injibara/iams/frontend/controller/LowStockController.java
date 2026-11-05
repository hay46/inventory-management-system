package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.model.Item;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class LowStockController {
    @FXML private TableView<Item> table;
    @FXML private TableColumn<Item, String> colCode, colName;
    @FXML private TableColumn<Item, Integer> colQty, colMin;

    private final ObservableList<Item> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        table.setItems(items);
        refresh();
    }

    @FXML
    public void refresh() {
        Thread t = new Thread(() -> {
            try {
                var resp = ApiClient.get("/api/items/low-stock");
                if (resp.statusCode() == 200) {
                    JsonNode arr = ApiClient.mapper().readTree(resp.body());
                    List<Item> list = new ArrayList<>();
                    for (JsonNode n : arr) {
                        Item it = new Item();
                        it.setItemCode(n.get("itemCode").asText());
                        it.setItemName(n.get("itemName").asText());
                        it.setQuantity(n.get("quantity").asInt());
                        it.setMinimumStock(n.get("minimumStock").asInt());
                        list.add(it);
                    }
                    Platform.runLater(() -> items.setAll(list));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true); t.start();
    }
}


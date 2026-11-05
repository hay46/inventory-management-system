package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.model.Txn;
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

public class TransactionsController {
    @FXML private TableView<Txn> table;
    @FXML private TableColumn<Txn, Long> colId;
    @FXML private TableColumn<Txn, String> colItem, colType, colDate, colUser;
    @FXML private TableColumn<Txn, Integer> colQty;

    private final ObservableList<Txn> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItem.setCellValueFactory(new PropertyValueFactory<>("item"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("by"));
        table.setItems(items);
        refresh();
    }

    @FXML
    public void refresh() {
        Thread t = new Thread(() -> {
            try {
                var resp = ApiClient.get("/api/stock/history");
                if (resp.statusCode() == 200) {
                    JsonNode arr = ApiClient.mapper().readTree(resp.body());
                    List<Txn> list = new ArrayList<>();
                    for (JsonNode n : arr) {
                        Txn tx = new Txn();
                        tx.setId(n.get("id").asLong());
                        JsonNode item = n.get("item");
                        tx.setItem(item != null && item.has("itemName") ? item.get("itemName").asText() : "");
                        tx.setType(n.get("type").asText());
                        tx.setQuantity(n.get("quantity").asInt());
                        tx.setDate(n.get("transactionDate").asText());
                        JsonNode by = n.get("performedBy");
                        tx.setBy(by != null && by.has("username") ? by.get("username").asText() : "");
                        list.add(tx);
                    }
                    Platform.runLater(() -> items.setAll(list));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true); t.start();
    }
}


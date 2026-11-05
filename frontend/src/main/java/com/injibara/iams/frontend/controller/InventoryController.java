package com.injibara.iams.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.injibara.iams.frontend.model.Item;
import com.injibara.iams.frontend.service.ApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryController {

    @FXML private TextField searchField;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> colCode, colName, colCategory, colLocation, colUnit, colStatus;
    @FXML private TableColumn<Item, Integer> colQty, colMin;

    private final ObservableList<Item> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("locationName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        itemTable.setItems(items);
        refresh();
    }

    @FXML
    public void refresh() { search(); }

    @FXML
    public void search() {
        String q = searchField.getText().trim();
        Thread t = new Thread(() -> {
            try {
                String path = "/api/items" + (q.isEmpty() ? "" : "?q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8));
                var resp = ApiClient.get(path);
                if (resp.statusCode() == 200) {
                    JsonNode arr = ApiClient.mapper().readTree(resp.body());
                    List<Item> list = new ArrayList<>();
                    for (JsonNode n : arr) {
                        Item it = new Item();
                        it.setId(n.get("id").asLong());
                        it.setItemCode(n.get("itemCode").asText());
                        it.setItemName(n.get("itemName").asText());
                        it.setDescription(n.has("description") && !n.get("description").isNull() ? n.get("description").asText() : "");
                        it.setCategoryId(n.has("categoryId") && !n.get("categoryId").isNull() ? n.get("categoryId").asLong() : null);
                        it.setCategoryName(n.has("categoryName") && !n.get("categoryName").isNull() ? n.get("categoryName").asText() : "");
                        it.setLocationId(n.has("locationId") && !n.get("locationId").isNull() ? n.get("locationId").asLong() : null);
                        it.setLocationName(n.has("locationName") && !n.get("locationName").isNull() ? n.get("locationName").asText() : "");
                        it.setUnit(n.has("unit") && !n.get("unit").isNull() ? n.get("unit").asText() : "Piece");
                        it.setQuantity(n.get("quantity").asInt());
                        it.setMinimumStock(n.get("minimumStock").asInt());
                        it.setStatus(n.get("status").asText());
                        list.add(it);
                    }
                    Platform.runLater(() -> { items.setAll(list); });
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void showAddDialog() {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Add Item");
        TextField code = new TextField(); code.setPromptText("Item code");
        TextField name = new TextField(); name.setPromptText("Item name");
        TextField desc = new TextField(); desc.setPromptText("Description");
        TextField catId = new TextField(); catId.setPromptText("Category ID (optional)");
        TextField locId = new TextField(); locId.setPromptText("Location ID (optional)");
        TextField unit = new TextField(); unit.setPromptText("Unit");
        TextField qty = new TextField(); qty.setPromptText("Quantity");
        TextField min = new TextField(); min.setPromptText("Minimum stock");
        VBox box = new VBox(8, code, name, desc, catId, locId, unit, qty, min);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Item it = new Item();
                it.setItemCode(code.getText().trim());
                it.setItemName(name.getText().trim());
                it.setDescription(desc.getText().trim());
                it.setCategoryId(parseLong(catId.getText()));
                it.setLocationId(parseLong(locId.getText()));
                it.setUnit(unit.getText().trim().isEmpty() ? "Piece" : unit.getText().trim());
                it.setQuantity(parseInt(qty.getText()));
                it.setMinimumStock(parseInt(min.getText()));
                return it;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(item -> createItem(item));
    }

    private void createItem(Item item) {
        Thread t = new Thread(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("itemCode", item.getItemCode());
                body.put("itemName", item.getItemName());
                body.put("description", item.getDescription());
                body.put("categoryId", item.getCategoryId());
                body.put("locationId", item.getLocationId());
                body.put("unit", item.getUnit());
                body.put("quantity", item.getQuantity());
                body.put("minimumStock", item.getMinimumStock());
                var resp = ApiClient.post("/api/items", body);
                Platform.runLater(() -> {
                    if (resp.statusCode() == 200 || resp.statusCode() == 201) refresh();
                    else showAlert("Error", resp.body());
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void editSelected() {
        Item sel = itemTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Info", "Select an item first"); return; }
        // Simple: re-open add dialog pre-filled not implemented; use update via prompt
        TextInputDialog d = new TextInputDialog(String.valueOf(sel.getQuantity()));
        d.setTitle("Adjust quantity (edit not full) - use Stock Adjustment");
        d.setHeaderText("Quick update minimum stock for " + sel.getItemName());
        d.setContentText("Minimum stock:");
        d.showAndWait().ifPresent(v -> {
            try {
                int min = Integer.parseInt(v.trim());
                Map<String, Object> body = new HashMap<>();
                body.put("itemCode", sel.getItemCode());
                body.put("itemName", sel.getItemName());
                body.put("description", sel.getDescription());
                body.put("categoryId", sel.getCategoryId());
                body.put("locationId", sel.getLocationId());
                body.put("unit", sel.getUnit());
                body.put("quantity", sel.getQuantity());
                body.put("minimumStock", min);
                Thread t = new Thread(() -> {
                    try {
                        var resp = ApiClient.put("/api/items/" + sel.getId(), body);
                        Platform.runLater(() -> { if (resp.statusCode() == 200) refresh(); else showAlert("Error", resp.body()); });
                    } catch (Exception e) { e.printStackTrace(); }
                });
                t.setDaemon(true); t.start();
            } catch (NumberFormatException e) { showAlert("Error", "Invalid number"); }
        });
    }

    @FXML
    public void deleteSelected() {
        Item sel = itemTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Info", "Select an item first"); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + sel.getItemName() + "?", ButtonType.YES, ButtonType.NO);
        c.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                Thread t = new Thread(() -> {
                    try {
                        var resp = ApiClient.delete("/api/items/" + sel.getId());
                        Platform.runLater(() -> { if (resp.statusCode() == 204) refresh(); else showAlert("Error", resp.body()); });
                    } catch (Exception e) { e.printStackTrace(); }
                });
                t.setDaemon(true); t.start();
            }
        });
    }

    private Long parseLong(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return null; }
    }
    private Integer parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}

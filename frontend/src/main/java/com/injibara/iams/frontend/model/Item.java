package com.injibara.iams.frontend.model;

public class Item {
    private Long id;
    private String itemCode;
    private String itemName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long locationId;
    private String locationName;
    private String unit;
    private Integer quantity;
    private Integer minimumStock;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String v) { this.itemCode = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long v) { this.categoryId = v; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String v) { this.categoryName = v; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long v) { this.locationId = v; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String v) { this.locationName = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }
    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer v) { this.minimumStock = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}

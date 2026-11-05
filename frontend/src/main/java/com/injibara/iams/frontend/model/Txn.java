package com.injibara.iams.frontend.model;

public class Txn {
    private Long id;
    private String item;
    private String type;
    private Integer quantity;
    private String date;
    private String by;

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }
    public String getItem() { return item; }
    public void setItem(String v) { item = v; }
    public String getType() { return type; }
    public void setType(String v) { type = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { quantity = v; }
    public String getDate() { return date; }
    public void setDate(String v) { date = v; }
    public String getBy() { return by; }
    public void setBy(String v) { by = v; }
}

package edu.uga.cs.roomateshoppingapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseRecord {
    private String id; // Unique identifier for the purchase record
    private double totalPrice;
    private String purchaserName;
    private Map<String, Double> itemDetails;

    public PurchaseRecord() {
        itemDetails = new HashMap<>();
    }

    public PurchaseRecord(double totalPrice, String purchaserName, Map<String, Double> itemDetails) {
        this.totalPrice = totalPrice;
        this.purchaserName = purchaserName;
        this.itemDetails = itemDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    public Map<String, Double> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(Map<String, Double> itemDetails) {
        this.itemDetails = itemDetails;
    }
}

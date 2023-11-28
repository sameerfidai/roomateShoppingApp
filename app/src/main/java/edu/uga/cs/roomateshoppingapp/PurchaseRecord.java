package edu.uga.cs.roomateshoppingapp;

import java.util.List;

public class PurchaseRecord {
    private double totalPrice;
    private String purchaserName;
    private List<String> itemNames;

    public PurchaseRecord() {

    }

    public PurchaseRecord(double totalPrice, String purchaserName, List<String> itemNames) {
        this.totalPrice = totalPrice;
        this.purchaserName = purchaserName;
        this.itemNames = itemNames;
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

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }
}

package edu.uga.cs.roomateshoppingapp;

public class ShoppingItem {
    private String id;
    private String name;
    private boolean isPurchased;
    private String purchaserId;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String name, boolean isPurchased, String purchaserId) {
        this.id = id;
        this.name = name;
        this.isPurchased = isPurchased;
        this.purchaserId = purchaserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getPurchaserId() {
        return purchaserId;
    }

    public void setPurchaserId(String purchaserId) {
        this.purchaserId = purchaserId;
    }
}

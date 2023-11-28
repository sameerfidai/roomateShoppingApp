package edu.uga.cs.roomateshoppingapp;

public class ShoppingItem {
    private String id;
    private String name;
    private boolean isPurchased;
    private boolean isInCart;
    private String purchaserId;
    private double price;
    private String purchaserName;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String name, boolean isPurchased, String purchaserId) {
        this.id = id;
        this.name = name;
        this.isPurchased = isPurchased;
        this.purchaserId = purchaserId;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
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

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isPurchased=" + isPurchased +
                ", purchaserId='" + purchaserId + '\'' +
                '}';
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

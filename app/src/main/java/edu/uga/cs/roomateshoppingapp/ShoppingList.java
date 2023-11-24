package edu.uga.cs.roomateshoppingapp;

public class ShoppingList {

    private String listId;
    private String ownerId;
    private String inviteCode;
    // You might want to add additional fields, such as a list of items, etc.

    // Default constructor for Firebase
    public ShoppingList() {
    }

    // Constructor with parameters
    public ShoppingList(String listId, String ownerId, String inviteCode) {
        this.listId = listId;
        this.ownerId = ownerId;
        this.inviteCode = inviteCode;
    }

    // Getters and Setters
    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    // Additional getters and setters for other fields
}

package edu.uga.cs.roomateshoppingapp;

public class ShoppingList {

    private String listId;
    private String ownerId;
    private String inviteCode;

    // Default constructor for Firebase
    public ShoppingList() {
    }

    public ShoppingList(String listId, String ownerId, String inviteCode) {
        this.listId = listId;
        this.ownerId = ownerId;
        this.inviteCode = inviteCode;
    }

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
}

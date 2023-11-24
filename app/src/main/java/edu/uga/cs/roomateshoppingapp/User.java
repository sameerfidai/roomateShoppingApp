package edu.uga.cs.roomateshoppingapp;

public class User {
    private String userId;
    private String name;
    private String email;
    private String listId;

    public User() {
    }

    public User(String userId, String name, String email, String listId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.listId = listId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

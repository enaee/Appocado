package com.jku.appocado.Models;

import java.util.ArrayList;

public class User {
    String userID;
    String name;
    ArrayList habits;

    public User() {
    }

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getUserHabits() {
        return habits;
    }
}

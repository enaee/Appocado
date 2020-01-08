package com.jku.appocado.Models;

import java.util.ArrayList;

public class DatabaseSnaphot {
    private ArrayList habitsLits;
    private ArrayList usersList;

    public ArrayList getHabitsLits() {
        return habitsLits;
    }

    public void setHabitsLits(ArrayList habitsLits) {
        this.habitsLits = habitsLits;
    }

    public ArrayList getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList usersList) {
        this.usersList = usersList;
    }

    public DatabaseSnaphot(ArrayList habitsLits, ArrayList usersList) {
        this.habitsLits = habitsLits;
        this.usersList = usersList;
    }
}

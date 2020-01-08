package com.jku.appocado.Models;

import java.util.ArrayList;

public class Habit {
    String id;
    String name;
    String description;
    ArrayList users;

    public Habit() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Habit(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Habit(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

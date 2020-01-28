package com.jku.appocado.Models;

public class Habit {
    String id;
    String name;
    String description;
    String image;
    String count;

    public Habit() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Habit(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public Habit(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

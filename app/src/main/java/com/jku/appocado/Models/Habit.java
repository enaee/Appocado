package com.jku.appocado.Models;

public class Habit {
    String id;
    String name;
    String description;
    String image;
    int count;

    public Habit(String name, String description, String image, int count) {

        this.name = name;
        this.description = description;
        this.image = image;
        this.count = count;
    }

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

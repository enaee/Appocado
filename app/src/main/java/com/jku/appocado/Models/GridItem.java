package com.jku.appocado.Models;

public class GridItem {

    String habitName;
    int habitImage;

    public GridItem(String habitName, int habitImage) {
        this.habitName = habitName;
        this.habitImage = habitImage;
    }

    public String getHabitName() {
        return habitName;
    }

    public int getHabitImage() {
        return habitImage;
    }
}

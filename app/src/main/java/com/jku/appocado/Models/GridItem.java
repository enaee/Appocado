package com.jku.appocado.Models;

public class GridItem {

    String habitDescription;
    String habitName;
    int habitImage;

    public GridItem(String habitName, int habitImage, String habitDescription) {
        this.habitName = habitName;
        this.habitImage = habitImage;
        this.habitDescription = habitDescription;
    }

    public String getHabitName() {
        return habitName;
    }

    public String getHabitDescription() {
        return habitDescription;
    }

    public int getHabitImage() {
        return habitImage;
    }
}

package com.jku.appocado.Models;

public class Score {
    private String lastInput;
    private int strike;
    private int totalActions;
    private int totalDays;

    public Score(String lastInput, int strike, int totalActions, int totalDays) {
        this.lastInput = lastInput;
        this.strike = strike;
        this.totalActions = totalActions;
        this.totalDays = totalDays;
    }


    public Score() {
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public String getLastInput() {
        return lastInput;
    }

    public void setLastInput(String lastInput) {
        this.lastInput = lastInput;
    }

    public int getStrike() {
        return strike;
    }

    public void setStrike(int strike) {
        this.strike = strike;
    }

    public int getTotalActions() {
        return totalActions;
    }

    public void setTotalActions(int totalActions) {
        this.totalActions = totalActions;
    }
}

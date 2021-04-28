package com.alon.imagerecognitionapp.Models;

public class RecognitionItem {

    private String name;
    private float match;

    public RecognitionItem() {
    }

    public RecognitionItem(String name, float match) {
        this.name = name;
        this.match = match;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMatch() {
        return match;
    }

    public void setMatch(float match) {
        this.match = match;
    }
}

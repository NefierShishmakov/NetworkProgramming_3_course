package ru.nsu.ccfit.shishmakov.json.placedescription;

public class PlaceDescription {
    public PlaceDescription(String title, String text) {
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Title: " + title + System.lineSeparator() + "Description: " + text;
    }

    private final String title;
    private final String text;
}

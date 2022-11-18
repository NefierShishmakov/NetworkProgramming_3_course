package ru.nsu.ccfit.shishmakov.json.placedescription;

public class PlaceInfo
{
    public PlaceInfo(String name, PlaceDescription wikipedia_extracts) {
        this.name = name;
        this.wikipedia_extracts = wikipedia_extracts;
    }

    @Override
    public String toString() {
        String first = (name == null || name.equals("")) ? "No name\n" : "The place name - " + name + System.lineSeparator();
        String second = wikipedia_extracts == null ? "No description" : wikipedia_extracts.toString();

        return first + second;
    }

    private final String name;
    private final PlaceDescription wikipedia_extracts;
}

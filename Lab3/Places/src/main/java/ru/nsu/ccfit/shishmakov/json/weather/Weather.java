package ru.nsu.ccfit.shishmakov.json.weather;

public class Weather
{
    public Weather(String description) {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "Weather - " + this.description;
    }

    private final String description;
}

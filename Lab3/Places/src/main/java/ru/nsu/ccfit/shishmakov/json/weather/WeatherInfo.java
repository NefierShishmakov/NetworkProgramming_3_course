package ru.nsu.ccfit.shishmakov.json.weather;

import java.util.ArrayList;

public class WeatherInfo
{
    public WeatherInfo(Temperature main, ArrayList<Weather> weather, Wind wind) {
        this.main = main;
        this.weather = weather;
        this.wind = wind;
    }

    @Override
    public String toString()
    {
        return "Weather info: " + weather.get(0) + ", " + main + ", " + wind;
    }

    private final Temperature main;
    private final ArrayList<Weather> weather;
    private final Wind wind;
}

package ru.nsu.ccfit.shishmakov.json.info;

import ru.nsu.ccfit.shishmakov.json.placedescription.PlaceInfo;
import ru.nsu.ccfit.shishmakov.json.weather.WeatherInfo;

import java.util.List;

public class Info
{
    public Info(WeatherInfo weatherInfo, List<PlaceInfo> placesInfo)
    {

        this.weatherInfo = weatherInfo;
        this.placesInfo = placesInfo;
    }

    public void printInfo()
    {
        System.out.println(weatherInfo);

        int index = 1;

        for (PlaceInfo placeInfo: placesInfo)
        {
            System.out.println(index++ + ") " + placeInfo);
        }
    }

    private final WeatherInfo weatherInfo;
    private final List<PlaceInfo> placesInfo;
}

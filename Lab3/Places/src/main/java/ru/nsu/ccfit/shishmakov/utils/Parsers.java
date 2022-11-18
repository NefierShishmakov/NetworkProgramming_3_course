package ru.nsu.ccfit.shishmakov.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ru.nsu.ccfit.shishmakov.json.interestingplaces.InterestingPlaces;
import ru.nsu.ccfit.shishmakov.json.location.LocationInfo;
import ru.nsu.ccfit.shishmakov.json.placedescription.PlaceInfo;
import ru.nsu.ccfit.shishmakov.json.weather.WeatherInfo;

import java.util.ArrayList;

public final class Parsers
{
    private static final Gson gson = new Gson();

    public static ArrayList<LocationInfo> locationParser(String strJson)
    {
        return gson.fromJson(gson.fromJson(strJson, JsonObject.class).getAsJsonArray("hits"),
                new TypeToken<ArrayList<LocationInfo>>(){}.getType());
    }

    public static WeatherInfo weatherInfoParser(String strJson)
    {
        return gson.fromJson(strJson, WeatherInfo.class);
    }

    public static ArrayList<InterestingPlaces> interestingPlacesParser(String strJson)
    {
        return gson.fromJson(gson.fromJson(strJson, JsonObject.class).getAsJsonArray("features"), new TypeToken<ArrayList<InterestingPlaces>>(){}.getType());
    }

    public static PlaceInfo placeDescriptionParser(String strJson)
    {
        return gson.fromJson(strJson, PlaceInfo.class);
    }
}

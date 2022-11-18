package ru.nsu.ccfit.shishmakov.utils;

import java.net.URI;
import java.net.URISyntaxException;

public final class URICreator
{
    private static final String LOCATION_API_KEY = "66f9be7a-3bfc-4f1f-af5b-5a736ea87b59";
    private static final String WEATHER_API_KEY = "2293ca36865f041a6671739f93ce167e";
    private static final String INTERESTING_PLACES_API_KEY = "5ae2e3f221c38a28845f05b6d32deaf48d7dbe597ce2272e95063518";
    private static final String PLACE_DESCRIPTION_API_KEY = "5ae2e3f221c38a28845f05b6d32deaf48d7dbe597ce2272e95063518";

    private static final int RADIUS_IN_METERS = 1000;

    private URICreator() {}

    public static URI getLocationURI(String location)
    {

        return URI.create("https://graphhopper.com/api/1/geocode?" + "q=" + location + "&limit=8" + "&key=" + LOCATION_API_KEY);
    }

    public static URI getWeatherURI(double latitude, double longitude)
    {
        return URI.create("http://api.openweathermap.org/data/2.5/weather?" + "lat=" + latitude + "&lon=" + longitude
        + "&appid=" + WEATHER_API_KEY);
    }

    public static URI getInterestingPlacesURI(double latitude, double longitude)
    {
        return URI.create("https://api.opentripmap.com/0.1/en/places/radius?" + "radius=" + RADIUS_IN_METERS +
                "&lon=" + longitude + "&lat=" + latitude + "&apikey=" + INTERESTING_PLACES_API_KEY);
    }

    public static URI getPlaceDescriptionURI(String placeXID)
    {
        return URI.create("https://api.opentripmap.com/0.1/ru/places/xid/" + placeXID + "?" +
                "apikey=" + PLACE_DESCRIPTION_API_KEY);
    }
}

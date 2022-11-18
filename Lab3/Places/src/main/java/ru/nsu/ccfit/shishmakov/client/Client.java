package ru.nsu.ccfit.shishmakov.client;

import ru.nsu.ccfit.shishmakov.json.info.Info;
import ru.nsu.ccfit.shishmakov.json.interestingplaces.InterestingPlaces;
import ru.nsu.ccfit.shishmakov.json.location.LocationInfo;
import ru.nsu.ccfit.shishmakov.json.location.Point;
import ru.nsu.ccfit.shishmakov.json.placedescription.PlaceInfo;
import ru.nsu.ccfit.shishmakov.json.weather.WeatherInfo;
import ru.nsu.ccfit.shishmakov.utils.Parsers;
import ru.nsu.ccfit.shishmakov.utils.URICreator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class Client
{
    private Client() {}

    public static CompletableFuture<ArrayList<LocationInfo>> getLocation(String location)
    {
        return httpClient.sendAsync(getHttpRequest(URICreator.getLocationURI(location)),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Parsers::locationParser);
    }

    public static CompletableFuture<Info> completeRemainingRequests(Point point)
    {
        CompletableFuture<WeatherInfo> weather = getWeather(point);
        CompletableFuture<ArrayList<InterestingPlaces>> interestingPlacesFuture = getInterestingPlaces(point);

        return interestingPlacesFuture.thenCompose(places -> sequence(places.stream().map(place ->
                getPlaceDescription(place.getXid())).collect(Collectors.toList()))).thenCombine(weather,
                (a, b) -> new Info(b, a));
    }

    public static CompletableFuture<WeatherInfo> getWeather(Point point)
    {
        return httpClient.sendAsync(getHttpRequest(URICreator.getWeatherURI(point.lat(), point.lng())),
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Parsers::weatherInfoParser);
    }

    public static CompletableFuture<ArrayList<InterestingPlaces>> getInterestingPlaces(Point point)
    {
        return httpClient.sendAsync(getHttpRequest(URICreator.getInterestingPlacesURI(point.lat(), point.lng())),
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Parsers::interestingPlacesParser);
    }

    public static CompletableFuture<PlaceInfo> getPlaceDescription(String xid)
    {
        return httpClient.sendAsync(getHttpRequest(URICreator.getPlaceDescriptionURI(xid)),
                HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Parsers::placeDescriptionParser);
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.<T>toList())
        );
    }

    private static HttpRequest getHttpRequest(URI uri)
    {
        return HttpRequest.newBuilder().uri(uri).GET().build();
    }

    private static final HttpClient httpClient = HttpClient.newHttpClient();
}
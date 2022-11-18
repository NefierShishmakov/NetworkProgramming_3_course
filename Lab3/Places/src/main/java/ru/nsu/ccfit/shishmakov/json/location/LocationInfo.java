package ru.nsu.ccfit.shishmakov.json.location;

public class LocationInfo
{
    public LocationInfo(String name, String country, String osm_value, Point point)
    {
        this.name = name;
        this.country = country;
        this.osm_value = osm_value;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public String toString()
    {
        return " Location info: " + "[" + "Name = " + this.name + ", " + "type = " + this.osm_value + ", "
                + "country = " + this.country  + "]" + System.lineSeparator() + this.point;
    }

    private final String name;
    private final String country;
    private final String osm_value;
    private final Point point;
}

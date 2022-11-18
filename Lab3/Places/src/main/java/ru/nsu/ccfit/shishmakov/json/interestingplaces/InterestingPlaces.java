package ru.nsu.ccfit.shishmakov.json.interestingplaces;

public class InterestingPlaces
{
    public InterestingPlaces(Properties properties) {
        this.properties = properties;
    }

    public String getXid()
    {
        return this.properties.getXid();
    }
    private final Properties properties;
}

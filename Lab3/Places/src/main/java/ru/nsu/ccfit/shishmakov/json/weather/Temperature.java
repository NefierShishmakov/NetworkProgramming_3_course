package ru.nsu.ccfit.shishmakov.json.weather;

public class Temperature
{
    public Temperature(double temp, double feels_like)
    {
        this.temp = temp;
        this.feels_like = feels_like;
    }

    @Override
    public String toString()
    {
        return "Temperature: " + "[" + "Current = " + this.temp + ", " + "feels like = " + this.feels_like + "]";
    }

    private final double temp;
    private final double feels_like;
}

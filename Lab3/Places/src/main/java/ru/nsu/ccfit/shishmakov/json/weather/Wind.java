package ru.nsu.ccfit.shishmakov.json.weather;

public class Wind
{
    public Wind(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString()
    {
        return "Wind speed - " + speed;
    }

    private final double speed;
}

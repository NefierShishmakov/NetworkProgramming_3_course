package ru.nsu.ccfit.shishmakov.json.location;

import java.util.Objects;

public final class Point {
    private final double lat;
    private final double lng;

    public Point(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "\tCoordinates: " + "[" + "lat=" + this.lat + ", " + "lng=" + this.lng + "]";
    }

    public double lat() {
        return lat;
    }

    public double lng() {
        return lng;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Point) obj;
        return Double.doubleToLongBits(this.lat) == Double.doubleToLongBits(that.lat) &&
                Double.doubleToLongBits(this.lng) == Double.doubleToLongBits(that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

}

package ru.nsu.ccfit.shishmakov.utils;

import ru.nsu.ccfit.shishmakov.json.location.LocationInfo;

import java.util.ArrayList;

public final class Printers
{
    private Printers() {}

    public static void printLocationInfo(ArrayList<LocationInfo> locations)
    {
        int beginIdx = 1;

        for (LocationInfo info: locations)
        {
            System.out.println(beginIdx++ + ") " + info);
        }
        System.out.println();
    }
}

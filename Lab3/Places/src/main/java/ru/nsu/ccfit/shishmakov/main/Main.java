package ru.nsu.ccfit.shishmakov.main;

import ru.nsu.ccfit.shishmakov.client.Client;
import ru.nsu.ccfit.shishmakov.json.info.Info;
import ru.nsu.ccfit.shishmakov.json.location.LocationInfo;
import ru.nsu.ccfit.shishmakov.utils.Printers;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main
{
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the location: ");

        String location = scanner.nextLine();

        ArrayList<LocationInfo> locations = Client.getLocation(location).get();

        if (locations == null || locations.isEmpty())
        {
            System.out.println("No such locations - " + location);
            return;
        }

        Printers.printLocationInfo(locations);

        System.out.print("Enter the location number you want to learn more about: ");

        Info info = Client.completeRemainingRequests(locations.get(scanner.nextInt() - 1).getPoint()).join();

        info.printInfo();
    }
}
package ru.nsu.ccfit.shishmakov.analyzer;

import java.util.HashMap;
import java.util.Map;

public class Analyzer implements Runnable {
    public Analyzer(HashMap<String, Boolean> availableIps)
    {
        this.availableIps = availableIps;
    }

    @Override
    public void run()
    {
        synchronized (availableIps)
        {
            for (Map.Entry<String, Boolean> entry: this.availableIps.entrySet())
            {
                if (!entry.getValue())
                {
                    System.out.println("The host with this ip address is not available: " + entry.getKey());
                    this.availableIps.remove(entry.getKey());
                }
                else
                {
                    this.availableIps.replace(entry.getKey(), true, false);
                }
            }
        }
    }

    private final HashMap<String, Boolean> availableIps;
}

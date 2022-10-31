package ru.nsu.ccfit.shishmakov.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.shishmakov.server.ServerRoutine;

public class Main
{
    private static final Logger mainLogger = LogManager.getLogger(Main.class);
    private static final int PORT_ARG_INDEX = 0;

    public static void main(String[] args)
    {
        ServerRoutine serverRoutine = new ServerRoutine(Integer.parseInt(args[PORT_ARG_INDEX]));

        mainLogger.info("Server started his routine");
        serverRoutine.startRoutine();
        mainLogger.info("Server ended his routine");
    }
}

package ru.nsu.ccfit.shishmakov.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.shishmakov.analyzer.FileAnalyzer;
import ru.nsu.ccfit.shishmakov.client.ClientRoutine;
import ru.nsu.ccfit.shishmakov.constants.ArgConstants;

public class Main
{
    private static final Logger mainLogger = LogManager.getLogger(Main.class);

    public static void main(String[] args)
    {
        if (!FileAnalyzer.isFileNameLengthCorrect(args[ArgConstants.FILE_PATH_ARG_INDEX]))
        {
            mainLogger.error("The file name length must be less or equal 4096 bytes");
            return;
        }
        else if (!FileAnalyzer.isFileSizeCorrect(args[ArgConstants.FILE_PATH_ARG_INDEX]))
        {
            mainLogger.error("The file size must be less or equal 1 terabyte");
            return;
        }

        ClientRoutine clientRoutine = new ClientRoutine(
                args[ArgConstants.FILE_PATH_ARG_INDEX],
                args[ArgConstants.SERVER_IP_ADDRESS_ARG_INDEX],
                Integer.parseInt(args[ArgConstants.SERVER_PORT_ARG_INDEX])
        );

        mainLogger.info("Client started his routine");
        clientRoutine.startRoutine();
        mainLogger.info("Client ended his routine");
    }
}

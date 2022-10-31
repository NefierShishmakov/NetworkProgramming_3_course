package ru.nsu.ccfit.shishmakov.main;

import ru.nsu.ccfit.shishmakov.constants.Constants;
import ru.nsu.ccfit.shishmakov.executor.Executor;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Executor executor = new Executor(args[Constants.GROUP_IP_ADDRESS_ARGS_INDEX]);
            executor.execute();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("Hello world");
    }
}

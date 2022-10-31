package ru.nsu.ccfit.shishmakov.utils;

public final class Utils
{
    private Utils() {}

    public static String getStrStackTrace(Exception ex)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(ex);
        builder.append(System.lineSeparator());

        for (StackTraceElement el: ex.getStackTrace())
        {
            builder.append(el.toString());
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}

package ru.nsu.ccfit.shishmakov.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileAnalyzer
{
    private static final int MAX_FILE_NAME_LENGTH = 4096;
    private static final long MAX_FILE_SIZE_IN_BYTES = 1099511627776L;
    public static boolean isFileNameLengthCorrect(String filePath)
    {
        return Paths.get(filePath).getFileName().toString().length() <= MAX_FILE_NAME_LENGTH;
    }

    public static boolean isFileSizeCorrect(String filePath)
    {
        long fileSize = 0;

        try
        {
            fileSize = Files.size(Paths.get(filePath));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }

        return fileSize <= MAX_FILE_SIZE_IN_BYTES;
    }
}

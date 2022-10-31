package ru.nsu.ccfit.shishmakov.utils;

import java.nio.file.Path;
import java.util.Objects;

public final class FileUtilities
{
    private FileUtilities() {}

    private static final String SEPARATOR = "/";

    public static String getStrPath(String left, String right)
    {
        return (left + SEPARATOR + right);
    }

    public static int getCreatedFilesNum(Path filesDir, String createdFileName)
    {
        return Objects.requireNonNull(filesDir.toFile().list((file, name) -> name.contains(createdFileName))).length;
    }
}

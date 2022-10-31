package ru.nsu.ccfit.shishmakov.exceptions;

public class FileCreationException extends CreationException
{
    public FileCreationException(String errorMessage, Throwable err)
    {
        super(errorMessage, err);
    }
}

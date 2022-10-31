package ru.nsu.ccfit.shishmakov.exceptions;

public class DirectoryCreationException extends CreationException
{
    public DirectoryCreationException(String errorMessage, Throwable err)
    {
        super(errorMessage, err);
    }
}

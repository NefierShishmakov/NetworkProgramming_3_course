package ru.nsu.ccfit.shishmakov.exceptions;

public class CreationException extends Exception
{
    public CreationException(String errorMessage, Throwable err)
    {
        super(errorMessage, err);
    }
}

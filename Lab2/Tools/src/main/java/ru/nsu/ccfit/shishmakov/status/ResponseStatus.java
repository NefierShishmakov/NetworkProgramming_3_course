package ru.nsu.ccfit.shishmakov.status;

public enum ResponseStatus
{
    FILE_CREATION_ERROR(123),
    FILE_CREATION_SUCCESS(90),
    FILE_TRANSFER_ERROR(111),
    FILE_TRANSFER_SUCCESS(203);

    ResponseStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return this.status;
    }

    private final int status;
}

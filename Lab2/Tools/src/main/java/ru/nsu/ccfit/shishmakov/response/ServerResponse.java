package ru.nsu.ccfit.shishmakov.response;

import java.io.Serializable;

public class ServerResponse implements Serializable
{
    public ServerResponse(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }

    private final int status;
}

package ru.nsu.ccfit.shishmakov.data;

import java.io.Serializable;

public class ClientFileData implements Serializable
{
    public ClientFileData(int bufferSize, boolean endOfData)
    {
        this.buffer = new byte[bufferSize];
        this.endOfData = endOfData;
    }

    public boolean isEndOfData()
    {
        return this.endOfData;
    }

    public byte[] getClientsData()
    {
        return this.buffer;
    }

    public void setReadBytesNum(int readBytesNum)
    {
        this.readBytesNum = readBytesNum;
    }

    public int getReadBytesNum()
    {
        return this.readBytesNum;
    }

    private final byte[] buffer;
    private int readBytesNum;
    private final boolean endOfData;
}

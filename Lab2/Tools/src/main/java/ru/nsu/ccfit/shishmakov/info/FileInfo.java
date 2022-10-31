package ru.nsu.ccfit.shishmakov.info;

import java.io.Serializable;

public class FileInfo implements Serializable
{
    public FileInfo(String fileName, long fileSize, int bufferSize)
    {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.bufferSize = bufferSize;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public int getBufferSize()
    {
        return this.bufferSize;
    }

    public long getFileSize()
    {
        return this.fileSize;
    }

    private final String fileName;
    private final long fileSize;
    private final int bufferSize;
}

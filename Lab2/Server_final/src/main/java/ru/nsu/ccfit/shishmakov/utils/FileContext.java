package ru.nsu.ccfit.shishmakov.utils;

import ru.nsu.ccfit.shishmakov.exceptions.DirectoryCreationException;
import ru.nsu.ccfit.shishmakov.exceptions.FileCreationException;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public final class FileContext implements Closeable
{
    private static final String FILE_NAME_SEPARATOR = "-";

    public FileContext(long actualFileSize, int bufferSize)
    {
        this.actualFileSize = actualFileSize;
        this.bufferSize = bufferSize;
    }

    public void createFilesDirectory(String filesDirectoryStrPath) throws DirectoryCreationException
    {
        try
        {
            this.createFilesDirectoryIfNecessary(filesDirectoryStrPath);
        }
        catch (IOException ex)
        {
            throw new DirectoryCreationException("Failed to create files directory " + "(" + filesDirectoryStrPath
                    + ")", ex);
        }
    }

    public String getFileName()
    {
        return this.filePath.getFileName().toString();
    }

    public void createFile(String fileName, String filesDirectoryStrPath) throws FileCreationException
    {
        try
        {
            this.fileCreation(fileName, filesDirectoryStrPath);
            this.fileOutputStream = new FileOutputStream(this.filePath.toFile());
        }
        catch (IOException ex)
        {
            throw new FileCreationException("Failed to create file " + "(" + fileName + ")", ex);
        }
    }

    public void addCurrentReadBytes(int readBytes)
    {
        this.currentReceivedBytes += readBytes;
    }

    public long getCurrentReceivedBytes()
    {
        long currReceivedBytes = this.currentReceivedBytes;
        this.currentReceivedBytes = 0;
        return currReceivedBytes;
    }

    public void addReadBytes(int readBytes)
    {
        this.allReceivedBytes += readBytes;
    }

    public long getAllReceivedBytes()
    {
        return this.allReceivedBytes;
    }

    public void writeReadBytesToFile(byte[] readBytes, int readBytesNum) throws IOException
    {
        this.fileOutputStream.write(readBytes, 0, readBytesNum);
    }

    public int getBufferSize()
    {
        return this.bufferSize;
    }

    public boolean isFileReceivedCorrectly()
    {
        return this.allReceivedBytes == this.actualFileSize;
    }

    private void createFilesDirectoryIfNecessary(String filesDirectoryStrPath) throws IOException
    {
        Path filesDirectoryPath = Paths.get(filesDirectoryStrPath);

        if (!this.isDirectoryExists(filesDirectoryPath))
        {
            Files.createDirectory(filesDirectoryPath);
        }
    }

    private void fileCreation(String fileName, String filesDirectoryStrPath) throws IOException
    {
        this.setPathOfFile(fileName, filesDirectoryStrPath);
        Files.createFile(this.filePath);
    }

    private void setPathOfFile(String fileName, String filesDirectoryStrPath)
    {
        Path intendedFilePath = Paths.get(FileUtilities.getStrPath(filesDirectoryStrPath, fileName));

        if (Files.exists(intendedFilePath))
        {
            intendedFilePath = this.getPathOfNewFile(intendedFilePath);
        }

        this.filePath = intendedFilePath;
    }

    private Path getPathOfNewFile(Path intendedFilePath)
    {
        String createdFileName = intendedFilePath.getFileName().toString();
        int createdFilesNum = FileUtilities.getCreatedFilesNum(intendedFilePath.getParent(), createdFileName);

        String newFileName = createdFilesNum + FILE_NAME_SEPARATOR + createdFileName;

        return Paths.get(FileUtilities.getStrPath(intendedFilePath.getParent().toString(), newFileName));
    }

    private boolean isDirectoryExists(Path filesDirectoryPath)
    {
        return Files.exists(filesDirectoryPath);
    }

    private final long actualFileSize;

    private final int bufferSize;

    private long allReceivedBytes = 0;

    private long currentReceivedBytes = 0;

    private Path filePath;
    private FileOutputStream fileOutputStream = null;

    @Override
    public void close() throws IOException
    {
        if (this.fileOutputStream != null)
        {
            this.fileOutputStream.close();
        }
    }
}

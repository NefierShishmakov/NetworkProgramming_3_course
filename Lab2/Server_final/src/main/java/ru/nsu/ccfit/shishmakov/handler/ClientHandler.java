package ru.nsu.ccfit.shishmakov.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.shishmakov.data.ClientFileData;
import ru.nsu.ccfit.shishmakov.exceptions.CreationException;
import ru.nsu.ccfit.shishmakov.info.FileInfo;
import ru.nsu.ccfit.shishmakov.response.ServerResponse;
import ru.nsu.ccfit.shishmakov.status.ResponseStatus;
import ru.nsu.ccfit.shishmakov.utils.FileContext;
import ru.nsu.ccfit.shishmakov.utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private static final Logger clientHandlerLogger = LogManager.getLogger(ClientHandler.class);

    private static final long TIME_TO_PRINT_SPEEDS_IN_MILLISECONDS = 3000L;

    public ClientHandler(Socket socket, String clientDirectoryStrPath, String clientIpAddress) throws IOException
    {
        this.socket = socket;
        this.clientDirectoryStrPath = clientDirectoryStrPath;
        this.clientIpAddress = clientIpAddress;
        this.clientRequestObjectReader = new ObjectInputStream(this.socket.getInputStream());
        this.serverResponseObjectWriter = new ObjectOutputStream(this.socket.getOutputStream());
        this.clientDataReader = new DataInputStream(this.socket.getInputStream());
    }

    @Override
    public void run()
    {
        try (this.socket)
        {
            clientHandlerLogger.info("Server started receiving FileInfo from the client with ip: " + this.clientIpAddress);
            FileInfo fileInfo = (FileInfo) this.clientRequestObjectReader.readObject();
            clientHandlerLogger.info("Server ended receiving FileInfo from the client with ip: " + this.clientIpAddress);

            try
            {
                this.prepareFileContextForReceivingData(fileInfo, this.clientDirectoryStrPath);
            }
            catch (CreationException ex)
            {
                clientHandlerLogger.error("Creation error" + Utils.getStrStackTrace(ex));

                clientHandlerLogger.info("Server started sending FILE_CREATION_ERROR response to client with ip - " + this.clientIpAddress);
                this.sendResponse(ResponseStatus.FILE_CREATION_ERROR.getStatus());
                clientHandlerLogger.info("Server ended sending FILE_CREATION_ERROR response to client with ip - " + this.clientIpAddress);
                return;
            }

            clientHandlerLogger.info("Server started sending FILE_CREATION_SUCCESS response to client with ip - " + this.clientIpAddress);
            this.sendResponse(ResponseStatus.FILE_CREATION_SUCCESS.getStatus());
            clientHandlerLogger.info("Server ended sending FILE_CREATION_SUCCESS response to client with ip - " + this.clientIpAddress);

            clientHandlerLogger.info("Server started receiving " + this.fileContext.getFileName() + " file " +
                    "from the client with ip - " + this.clientIpAddress);
            this.readFileFromClient(this.fileContext);
            clientHandlerLogger.info("Server ended receiving " + this.fileContext.getFileName() + " file " +
                    "from the client with ip - " + this.clientIpAddress);

            int final_status = this.fileContext.isFileReceivedCorrectly() ?
                    ResponseStatus.FILE_TRANSFER_SUCCESS.getStatus() :
                    ResponseStatus.FILE_TRANSFER_ERROR.getStatus();

            clientHandlerLogger.info("Server started sending status about file receiving to the client with ip - " + this.clientIpAddress);
            this.sendResponse(final_status);
            clientHandlerLogger.info("Server ended sending status about file receiving to the client with ip - " + this.clientIpAddress);
        }
        catch (IOException ex)
        {
            clientHandlerLogger.error("Client " + "(" + this.clientIpAddress + ")" + " socket exception"
            + Utils.getStrStackTrace(ex));
        }
        catch (ClassNotFoundException ex)
        {
            clientHandlerLogger.error("Class not found error" + Utils.getStrStackTrace(ex));
        }
        finally
        {
            try
            {
                this.fileContext.close();
            }
            catch (IOException ex)
            {
                clientHandlerLogger.error("Failed to close file output stream in fileContext for the client with ip - "
                + this.clientIpAddress + Utils.getStrStackTrace(ex));
            }

            clientHandlerLogger.info("Server stopped serving the client with ip - " + this.clientIpAddress);
        }
    }

    private void sendResponse(int responseStatus) throws IOException
    {
        ServerResponse response = new ServerResponse(responseStatus);
        this.serverResponseObjectWriter.writeObject(response);
        this.serverResponseObjectWriter.flush();
    }

    private void prepareFileContextForReceivingData(FileInfo fileInfo,
                                                    String filesDirectoryStrPath) throws CreationException
    {
        this.fileContext = new FileContext(fileInfo.getFileSize(), fileInfo.getBufferSize());

        fileContext.createFilesDirectory(filesDirectoryStrPath);
        fileContext.createFile(fileInfo.getFileName(), filesDirectoryStrPath);
    }

    private void readFileFromClient(FileContext fileContext) throws IOException, ClassNotFoundException
    {
        this.startTimeOfReadingFile = System.currentTimeMillis();

        while (true)
        {
            ClientFileData clientFileData = (ClientFileData) clientRequestObjectReader.readObject();

            if (clientFileData.isEndOfData())
            {
                break;
            }

            int readBytes = clientFileData.getReadBytesNum();

            fileContext.addReadBytes(readBytes);
            fileContext.addCurrentReadBytes(readBytes);
            fileContext.writeReadBytesToFile(clientFileData.getClientsData(), readBytes);

            if ((System.currentTimeMillis() - this.startTimeOfReadingFile) >= TIME_TO_PRINT_SPEEDS_IN_MILLISECONDS)
            {
                this.printSpeeds(System.currentTimeMillis() - this.startTimeOfReadingFile);
            }
        }

        if (this.allPassedTime == 0)
        {
            this.printSpeeds(System.currentTimeMillis() - this.startTimeOfReadingFile);
        }
    }

    private void printSpeeds(long passedTime)
    {
        long instantaneousSpeed = this.fileContext.getCurrentReceivedBytes() * 1000 / passedTime;
        this.allPassedTime += passedTime;
        long averageSpeed = this.fileContext.getAllReceivedBytes() * 1000 / this.allPassedTime;

        clientHandlerLogger.info("Instantaneous speed for client " + "(" + this.clientIpAddress + ")" +
                " is: " + instantaneousSpeed + "B/s");
        clientHandlerLogger.info("Average speed for client " + "(" + this.clientIpAddress + ")" +
                " is: " + averageSpeed + "B/s");

        this.startTimeOfReadingFile = System.currentTimeMillis();
    }

    private final Socket socket;

    private final ObjectInputStream clientRequestObjectReader;
    private final ObjectOutputStream serverResponseObjectWriter;
    private final DataInputStream clientDataReader;

    private final String clientDirectoryStrPath;
    private final String clientIpAddress;
    private FileContext fileContext = null;

    private long startTimeOfReadingFile;
    private long allPassedTime = 0;
}

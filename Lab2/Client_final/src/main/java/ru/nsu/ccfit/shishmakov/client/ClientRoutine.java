package ru.nsu.ccfit.shishmakov.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.shishmakov.data.ClientFileData;
import ru.nsu.ccfit.shishmakov.info.FileInfo;
import ru.nsu.ccfit.shishmakov.response.ServerResponse;
import ru.nsu.ccfit.shishmakov.status.ResponseStatus;
import ru.nsu.ccfit.shishmakov.utils.Utils;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientRoutine
{
    private static final Logger clientLogger = LogManager.getLogger(ClientRoutine.class);

    private static final int BUFFER_SIZE = 256;
    public ClientRoutine(String strFilePath, String serverIpAddress, int serverPort)
    {
        this.filePath = Paths.get(strFilePath);
        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
    }

    public void startRoutine()
    {
        try (Socket socket = new Socket(this.serverIpAddress, this.serverPort);
            FileInputStream fileInputStream = new FileInputStream(this.filePath.toFile()))
        {
            this.addShutdownHook(socket, fileInputStream);

            String clientIpAddress = socket.getInetAddress().getHostAddress();
            clientLogger.info("Client ip: " + clientIpAddress);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());;

            FileInfo fileInfo = new FileInfo(this.filePath.getFileName().toString(),
                    Files.size(this.filePath), BUFFER_SIZE);

            clientLogger.info("Client started sending file info to server");
            objectOutputStream.writeObject(fileInfo);
            objectOutputStream.flush();
            clientLogger.info("Client ended sending file info to server");

            clientLogger.info("Client started receiving response from the server");
            ServerResponse serverResponse = (ServerResponse) objectInputStream.readObject();
            clientLogger.info("Client ended receiving response from the server");

            if (serverResponse.getStatus() == ResponseStatus.FILE_CREATION_ERROR.getStatus())
            {
                clientLogger.error("Error with creation file on server");
                return;
            }

            clientLogger.info("Client started sending file to server");
            this.sendFileToServer(objectOutputStream, fileInputStream);
            clientLogger.info("Client ended sending file to server");

            clientLogger.info("Client started receiving response about file transfer from server");
            ServerResponse finalServerResponse = (ServerResponse) objectInputStream.readObject();
            clientLogger.info("Client ended receiving response about file transfer from server");

            int finalServerResponseStatus = finalServerResponse.getStatus();

            if (finalServerResponseStatus == ResponseStatus.FILE_TRANSFER_SUCCESS.getStatus())
            {
                clientLogger.info("File being transferred successfully");
            }
            else
            {
                clientLogger.info("File was not being transferred successfully");
            }
        }
        catch (IOException ex)
        {
            clientLogger.error("Client socket exception" + Utils.getStrStackTrace(ex));
        }
        catch (ClassNotFoundException ex)
        {
            clientLogger.error("Class not found error" + Utils.getStrStackTrace(ex));
        }
    }

    private void sendFileToServer(ObjectOutputStream clientFileDataObject, FileInputStream fileInputStream) throws IOException
    {
        ClientFileData clientFileData = new ClientFileData(BUFFER_SIZE, false);

        int readBytes;

        while ((readBytes = fileInputStream.read(clientFileData.getClientsData(), 0, BUFFER_SIZE)) != -1)
        {
            clientFileData.setReadBytesNum(readBytes);
            clientFileDataObject.writeObject(clientFileData);
            clientFileDataObject.flush();
        }

        ClientFileData endOfData = new ClientFileData(0, true);
        clientFileDataObject.writeObject(endOfData);
        clientFileDataObject.flush();
    }

    private void addShutdownHook(Socket socket, FileInputStream fileInputStream)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                fileInputStream.close();
            }
            catch (IOException ex)
            {
                clientLogger.error("Closing file input stream error" + Utils.getStrStackTrace(ex));
            }

            try
            {
                socket.close();
            }
            catch (IOException ex)
            {
                clientLogger.error("Closing client's socket error" + Utils.getStrStackTrace(ex));
            }
        }));
    }

    private final Path filePath;
    private final String serverIpAddress;
    private final int serverPort;

}

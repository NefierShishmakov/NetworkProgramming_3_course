package ru.nsu.ccfit.shishmakov.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.shishmakov.exceptions.DirectoryCreationException;
import ru.nsu.ccfit.shishmakov.handler.ClientHandler;
import ru.nsu.ccfit.shishmakov.utils.FileUtilities;
import ru.nsu.ccfit.shishmakov.utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerRoutine
{
    private static final Logger serverLogger = LogManager.getLogger(ServerRoutine.class);

    private static final int SERVER_SOCKET_BACKLOG = 20;

    private static final int CLIENT_SOCKET_TIMEOUT_IN_MILLISECONDS = 35000;

    private static final int TERMINATION_AWAIT_IN_SECONDS = 20;

    private static final String SERVER_DIRECTORY_STR_PATH = "uploads";

    public ServerRoutine(int port)
    {
        this.port = port;
    }

    public void startRoutine()
    {
        try (ServerSocket serverSocket = new ServerSocket(this.port, SERVER_SOCKET_BACKLOG))
        {
            this.createServersDirectoryIfNecessary();
            ExecutorService service = Executors.newCachedThreadPool();

            this.addShutdownHook(serverSocket, service);

            while (true)
            {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(CLIENT_SOCKET_TIMEOUT_IN_MILLISECONDS);
                String clientIpAddress = socket.getInetAddress().getHostAddress();

                serverLogger.info("Client with ip - " + clientIpAddress + " has been connected");

                service.execute(new ClientHandler(socket, FileUtilities.getStrPath(SERVER_DIRECTORY_STR_PATH,
                        clientIpAddress), clientIpAddress));
            }
        }
        catch (IOException ex)
        {
            serverLogger.error("Server socket exception" + Utils.getStrStackTrace(ex));
        }
        catch (DirectoryCreationException ex)
        {
            serverLogger.error("Failed to created server's directory" + Utils.getStrStackTrace(ex));
        }
    }

    private void createServersDirectoryIfNecessary() throws DirectoryCreationException
    {
        Path serverDirectoryPath = Paths.get(SERVER_DIRECTORY_STR_PATH);

        if (!Files.exists(serverDirectoryPath))
        {
            try
            {
                Files.createDirectory(serverDirectoryPath);
            }
            catch (IOException ex)
            {
                throw new DirectoryCreationException("Failed to created server's directory", ex);
            }
        }
    }

    private void addShutdownHook(ServerSocket serverSocket, ExecutorService service)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                boolean allTasksAreTerminated = service.awaitTermination(TERMINATION_AWAIT_IN_SECONDS, TimeUnit.SECONDS);

                if (!allTasksAreTerminated)
                {
                    service.shutdown();
                }
            }
            catch (InterruptedException ex)
            {
                serverLogger.error("Interrupted error" + Utils.getStrStackTrace(ex));
            }

            try
            {
                serverSocket.close();
            }
            catch (IOException ex)
            {
                serverLogger.error("Failed to close server's socket" + Utils.getStrStackTrace(ex));
            }
        }));
    }

    private final int port;
}

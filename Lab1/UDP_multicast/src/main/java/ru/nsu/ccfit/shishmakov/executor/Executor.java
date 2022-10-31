package ru.nsu.ccfit.shishmakov.executor;

import ru.nsu.ccfit.shishmakov.analyzer.Analyzer;
import ru.nsu.ccfit.shishmakov.receiver.Receiver;
import ru.nsu.ccfit.shishmakov.sender.Sender;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Executor
{
    public Executor(String groupIpAddress) throws IOException
    {
        this.availableIps = new HashMap<>();
        this.groupIpAddress = groupIpAddress;
        this.senderService = Executors.newSingleThreadScheduledExecutor();
        this.analyzerService = Executors.newSingleThreadScheduledExecutor();
        this.receiverThread = new Thread(new Receiver(this.groupIpAddress, this.availableIps));

    }

    public void execute() throws IOException
    {
        this.senderService.scheduleAtFixedRate(new Sender(this.groupIpAddress), 0, 1, TimeUnit.SECONDS);
        this.receiverThread.start();
        this.analyzerService.scheduleAtFixedRate(new Analyzer(this.availableIps), 0, 5, TimeUnit.SECONDS);
    }

    private final Thread receiverThread;

    private final ScheduledExecutorService senderService;
    private final ScheduledExecutorService analyzerService;
    private final String groupIpAddress;
    private final HashMap<String, Boolean> availableIps;
}

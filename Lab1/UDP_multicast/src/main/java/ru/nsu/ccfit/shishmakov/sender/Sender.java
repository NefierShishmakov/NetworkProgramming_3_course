package ru.nsu.ccfit.shishmakov.sender;

import ru.nsu.ccfit.shishmakov.constants.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender implements Runnable
{
    private final static String SENDER_MESSAGE = "Hello, this is a message from the sender thread with ip: ";

    public Sender(String groupIpAddress) throws IOException {
        this.socket = new MulticastSocket();
        this.packet = new DatagramPacket(SENDER_MESSAGE.getBytes(), SENDER_MESSAGE.length(),
                InetAddress.getByName(groupIpAddress), Constants.PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(this.socket::close));
    }

    @Override
    public void run()
    {
        try
        {
            socket.send(packet);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private final MulticastSocket socket;
    private final DatagramPacket packet;
}

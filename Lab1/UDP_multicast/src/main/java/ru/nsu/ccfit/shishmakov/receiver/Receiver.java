package ru.nsu.ccfit.shishmakov.receiver;

import ru.nsu.ccfit.shishmakov.constants.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashMap;


public class Receiver implements Runnable
{
    private static final int SENDER_MESSAGE_SIZE = 1024;

    public Receiver(String groupIpAddress, HashMap<String, Boolean> availableIps) throws IOException
    {
        this.availableIps = availableIps;
        this.socket = new MulticastSocket(Constants.PORT);
        this.socket.joinGroup(new InetSocketAddress(groupIpAddress, Constants.PORT), null);
        this.packet = new DatagramPacket(this.buffer, SENDER_MESSAGE_SIZE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                this.socket.leaveGroup(new InetSocketAddress(groupIpAddress, Constants.PORT), null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.socket.close();
        }));
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                this.socket.receive(this.packet);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                break;
            }

            String senderIp = this.packet.getAddress().toString();

            System.out.println(new String(this.packet.getData(), 0, this.packet.getLength()) + senderIp);

            synchronized (this.availableIps)
            {
                if (!this.availableIps.containsKey(senderIp))
                {
                    System.out.println("The new host has arrived with ip: " + senderIp);
                    this.availableIps.put(senderIp, true);
                }
                else
                {
                    this.availableIps.replace(senderIp, false, true);
                }
            }
        }
    }

    private final MulticastSocket socket;
    private final DatagramPacket packet;

    private final HashMap<String, Boolean> availableIps;
    private final byte[] buffer = new byte[SENDER_MESSAGE_SIZE];
}

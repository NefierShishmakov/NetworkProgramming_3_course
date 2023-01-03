package ru.nsu.ccfit.shishmakov.handlers;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;
import ru.nsu.ccfit.shishmakov.dns.DnsResolver;
import ru.nsu.ccfit.shishmakov.dns.Info;
import ru.nsu.ccfit.shishmakov.protocol.ConnectionRequestMessage;
import ru.nsu.ccfit.shishmakov.protocol.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ConnectRequestHandler implements Handler {
    @Override
    public void handle(SelectionKey key) {
        boolean hasError = readConnectRequest(key);

        if (hasError) {
            System.err.println("connect request: error" + key);
            return;
        }

        System.err.println("connect request success + " + key);
    }

    private boolean readConnectRequest(SelectionKey key) {
        CompleteAttachment attachment = (CompleteAttachment) key.attachment();
        SocketChannel clientChannel = (SocketChannel) key.channel();

        attachment.keyState = KeyState.CONNECT_RESPONSE_SUCCESS;
        ByteBuffer buffer = (ByteBuffer) attachment.getOut();

        if (attachment.remoteAddress != null) {
            try {
                connectToServer(key, attachment);
                System.err.println("connect" + key);
                return false;
            }

            catch (Exception e) {
                key.interestOps(SelectionKey.OP_WRITE);
                attachment.keyState = KeyState.CONNECT_RESPONSE_FAILED;
                return true;
            }
        }

        buffer.clear();

        try {
            int readResult = clientChannel.read(buffer);

            if (readResult == -1) {
                closeChannel((SocketChannel) key.channel());
                System.err.println("readResult == -1 : " + key);
                return true;
            }

            if (!isFullMessage(buffer)) {
                System.err.println("Not full message " + key);
                return false;
            }

            System.err.println("READ: " + buffer + " " + key);

            buffer.flip();
        }

        catch (IOException e) {
            System.err.println("IOEXECTION" + key);
            closeChannel((SocketChannel) key.channel());
            return true;
        }

            ConnectionRequestMessage connectionRequestMessage = parseConnectRequest(buffer);

            key.interestOps(SelectionKey.OP_WRITE);

            if (connectionRequestMessage == null)  {
                attachment.keyState = KeyState.CONNECT_RESPONSE_FAILED;
                System.err.println("connectionRequestMessage == null" + key);
                return true;
            }

            System.err.println("Connected: address = " + new String(connectionRequestMessage.address()) + " port = " + connectionRequestMessage.getPortAsInt());

            switch (connectionRequestMessage.addressType()) {
                case Protocol.IPv4 -> {
                    try {
                        attachment.remoteAddress =
                                new InetSocketAddress(InetAddress.getByAddress(connectionRequestMessage.address()),
                                                    connectionRequestMessage.getPortAsInt());
                    } catch (UnknownHostException e) {
                        throw new RuntimeException();
                    }
                    try {
                        connectToServer(key, attachment);
                    }

                    catch (Exception e) {
                        System.err.println("CONNECT ERROR" + key);
                        attachment.keyState = KeyState.CONNECT_RESPONSE_FAILED;
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
                case Protocol.IPv6 -> attachment.keyState = KeyState.CONNECT_RESPONSE_UNAVAILABLE;

                case Protocol.DOMAIN -> {
                    key.interestOps(0);

                    DnsResolver resolver = DnsResolver.getInstance();

                    resolver.resolveDomain(new String(connectionRequestMessage.address(), StandardCharsets.ISO_8859_1),
                            new Info(key, connectionRequestMessage.getPortAsInt()));

                    attachment.keyState = KeyState.DNS_RESPONSE;
                }

                default -> throw new RuntimeException();
            }
            return false;

    }

    public void connectToServer(SelectionKey key, CompleteAttachment attachment) throws IOException {
        SocketChannel remoteChannel;
        remoteChannel = SocketChannel.open();
        remoteChannel.configureBlocking(false);

        remoteChannel.connect(attachment.remoteAddress);

        CompleteAttachment remoteChannelAttachment = new CompleteAttachment(KeyState.FINISH_CONNECT, false);

        remoteChannelAttachment.joinChannels(key);

        remoteChannelAttachment.setRemoteChannel((SocketChannel) key.channel());

        attachment.setRemoteChannel(remoteChannel);

        key.interestOps(0);

        remoteChannel.register(key.selector(), SelectionKey.OP_CONNECT, remoteChannelAttachment);
    }

    private ConnectionRequestMessage parseConnectRequest(ByteBuffer buf) {
        if (buf.get(0) != Protocol.VERSION) return null;

        if (buf.get(1) != Protocol.TCP_ESTABLISH_CONNECTION) return null;

        if (buf.get(2) != Protocol.RESERVED) return null;

        byte addressType = buf.get(3);

        switch (addressType) {
            case Protocol.IPv4 -> {
                System.err.println("IPV4");
                if (buf.limit() != 10) return null;
                return new ConnectionRequestMessage(buf.get(0),
                        buf.get(1),
                        buf.get(2),
                        buf.get(3),
                        Arrays.copyOfRange(buf.array(), 4, 8),
                        Arrays.copyOfRange(buf.array(), 8, 10));
            }

            case Protocol.DOMAIN -> {
                System.err.println("Domain");
                int len = buf.get(4);
                if (buf.limit() != 5 + len + 2) return null;

                return new ConnectionRequestMessage(buf.get(0),
                        buf.get(1),
                        buf.get(2),
                        buf.get(3),
                        Arrays.copyOfRange(buf.array(), 5, 5 + len),
                        Arrays.copyOfRange(buf.array(), 5 + len, 5 + len + 2));
            }

            case Protocol.IPv6 -> {
                if (buf.limit() != 18) return null;
                return new ConnectionRequestMessage(buf.get(0),
                        buf.get(1),
                        buf.get(2),
                        buf.get(3),
                        Arrays.copyOfRange(buf.array(), 4, 20),
                        Arrays.copyOfRange(buf.array(), 20, 22));
            }

            default -> throw new RuntimeException();
        }
    }

    boolean isFullMessage(ByteBuffer buf) {
        if (buf.position() < 4) return false;

        byte addressType = buf.get(3);

        switch (addressType) {
            case Protocol.IPv4 -> {
                return buf.position() == 10;
            }

            case Protocol.DOMAIN -> {
                int len = buf.get(4);
                return  buf.position() != 5 + len + 1 + 2;
            }

            case Protocol.IPv6 -> {
                return buf.position() != 18;
            }
        }

        throw new RuntimeException();
    }
}

package ru.nsu.ccfit.shishmakov;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.DomainAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;
import ru.nsu.ccfit.shishmakov.dns.DnsResolver;
import ru.nsu.ccfit.shishmakov.handlers.Handler;
import ru.nsu.ccfit.shishmakov.handlers.HandlerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
public class Proxy {
    private final Selector selector;
    private final int port;
    private boolean isRunning;

    private void initDatagramChannel() {
        try {
            DatagramChannel udpChannel = DatagramChannel.open();
            udpChannel.configureBlocking(false);
            udpChannel.register(selector, SelectionKey.OP_READ, new DomainAttachment(KeyState.DNS_RESPONSE));
            // Регистрирует канал в селекторе
            DnsResolver.create(udpChannel);  // init domain name resolver
        }
        catch (IOException e) {
            throw new RuntimeException("can't init datagram channel");
        }
    }

    private void initServerChannel() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new CompleteAttachment(KeyState.ACCEPT, true));
        }
        catch (IOException e) {
            throw new RuntimeException("can't init server channel");
        }
    }

    private Proxy(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
        initDatagramChannel(); // создаёт канал для dns resolving
        initServerChannel(); // создаёт канал для подключения клиентов к прокси, т.е. по сути устанавливает TCP соединения
    }

    public static Proxy getProxy(int port) {
        try {
            return new Proxy(port);
        }
        catch (IOException e) {
            return null;
        }
    }

    public void start() throws IOException {
        isRunning = true;
        System.err.println("Server start");

        while (isRunning) {
            while (selector.select() > 0) {
                selector.selectedKeys().forEach(key -> {
                    if (key.isValid()) {
                        handle(key);
                    }
                });
                selector.selectedKeys().clear();
            }
        }
    }

    private void handle(SelectionKey key) {
        // обработка каждого ключа
        Handler handler = HandlerFactory.getHandler(key); // возвращает конкретный handler взависимости от ключа
        handler.handle(key); // обработка ключа
    }
}

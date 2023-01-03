package ru.nsu.ccfit.shishmakov.handlers;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler implements Handler {
    @Override
    public void handle(SelectionKey key) {
        boolean hasError = accept(key);

        if (hasError) {
            System.err.println("Couldn't accept client");
            return;
        }

        System.err.println("Client accepted");
    }

    private boolean accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel client;

        try {
            client = serverSocketChannel.accept();
        } catch (IOException e) {
            return true;
        }

        try {
            client.configureBlocking(false);
            client.register(key.selector(), SelectionKey.OP_READ, new CompleteAttachment(KeyState.INIT_REQUEST, true));
        }

        catch (IOException e) {
            try {
                client.close();
            } catch (IOException ex) {
                throw new RuntimeException("Couldn't close client channel");
            }
            return true;
        }

        return false;
    }
}
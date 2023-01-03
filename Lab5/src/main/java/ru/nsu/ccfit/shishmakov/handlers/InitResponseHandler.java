package ru.nsu.ccfit.shishmakov.handlers;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;
import ru.nsu.ccfit.shishmakov.protocol.InitResponseMessage;
import ru.nsu.ccfit.shishmakov.protocol.MessageBuilder;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class InitResponseHandler implements Handler {
    @Override
    public void handle(SelectionKey key) {
        boolean hasError = writeInitResponse(key);

        if (hasError) {
           System.err.println("Couldn't write init response from client");
           return;
        }

        System.err.println("Init response was successfully write");
    }

    private boolean writeInitResponse(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        CompleteAttachment completeAttachment = (CompleteAttachment) key.attachment();

        InitResponseMessage msg = MessageBuilder.buildInitResponseMessage(completeAttachment);

        try {
            clientChannel.write(msg.toBytes());
        } catch (IOException e) {
            key.cancel();
            closeChannel(clientChannel);
            return true;
        }

        completeAttachment.keyState = KeyState.CONNECT_REQUEST;
        key.interestOps(SelectionKey.OP_READ);

        completeAttachment.getOut().clear();
        completeAttachment.getIn().clear();
        return false;
    }
}

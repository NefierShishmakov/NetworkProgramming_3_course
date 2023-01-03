package ru.nsu.ccfit.shishmakov.handlers;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class FinishConnectHandler implements Handler {
    @Override
    public void handle(SelectionKey key) {

        SocketChannel proxyToServerChannel = (SocketChannel) key.channel();

        try {
            proxyToServerChannel.finishConnect();
        } catch (IOException e) {
            key.cancel();
            registerOnConnectionResponse(KeyState.CONNECT_RESPONSE_FAILED,
                    ((CompleteAttachment) key.attachment()).getRemoteChannel().keyFor(key.selector()));
            System.err.println("Finish connect error");
            return;
        }

        registerOnConnectionResponse(KeyState.CONNECT_RESPONSE_SUCCESS,
                ((CompleteAttachment) key.attachment()).getRemoteChannel().keyFor(key.selector()));

        System.err.println("Finish connect success");
    }

    private void registerOnConnectionResponse(KeyState state, SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
        CompleteAttachment attachment = (CompleteAttachment) key.attachment();
        attachment.keyState = state;
    }
}
package ru.nsu.ccfit.shishmakov.handlers;

import ru.nsu.ccfit.shishmakov.attachment.CompleteAttachment;
import ru.nsu.ccfit.shishmakov.attachment.KeyState;
import ru.nsu.ccfit.shishmakov.protocol.MessageBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ConnectResponseHandler implements Handler {

    @Override
    public void handle(SelectionKey key) {
        boolean hasError = writeConnectResponse(key);

        if (hasError) {
            System.err.println("Couldn't write connect response");
            return;
        }

        System.err.println("Connect response was successfully write");
    }

    private boolean writeConnectResponse(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        CompleteAttachment completeAttachment = (CompleteAttachment) key.attachment();

        System.err.println("buildConnectResponseMessage : " + key);

        ByteBuffer buffer = (ByteBuffer) completeAttachment.getIn();

        if (!completeAttachment.isRespWroteToBuffer) {
            buffer = MessageBuilder.buildConnectResponseMessage(completeAttachment);
            completeAttachment.isRespWroteToBuffer = true;
        }

        try {
            clientChannel.write(buffer); // содержится CONNECT_RESPONSE_FAILED OR CONNECT_RESPONSE_SUCCESS
        } catch (IOException e) {
            key.cancel();
            closeChannel(clientChannel);
            return true;
        }

        if (completeAttachment.keyState == KeyState.CONNECT_RESPONSE_SUCCESS) {
            SelectableChannel remoteChannel = ((CompleteAttachment) key.attachment()).getRemoteChannel();

            SelectionKey remoteKey = remoteChannel.keyFor(key.selector());

            ((CompleteAttachment) remoteKey.attachment()).keyState = KeyState.PROXYING;
            completeAttachment.keyState = KeyState.PROXYING;

            remoteKey.interestOps(SelectionKey.OP_READ);
            key.interestOps(SelectionKey.OP_READ);

            completeAttachment.getOut().clear();
            completeAttachment.getIn().clear();
        }
        else {
            key.cancel();
            closeChannel((SocketChannel) key.channel());
        }

        return false;
    }
}
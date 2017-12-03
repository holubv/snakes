package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class ChatMessage extends DefinedPacket {

    private int senderId;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(int senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    @Override
    public void read(ByteBuf buf) {
        senderId = buf.readUnsignedShort();
        message = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeShort(senderId);
        writeString(message, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "senderId=" + senderId +
                ", message='" + message + '\'' +
                '}';
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }
}

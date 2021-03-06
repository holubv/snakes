package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Serverbound
public class Chat extends DefinedPacket {

    private String message;

    public Chat() {
    }

    public Chat(String message) {
        this.message = message;
    }

    @Override
    public void read(ByteBuf buf) {
        message = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(message, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }
}

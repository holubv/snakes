package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class Disconnect extends DefinedPacket {

    private String reason = "";

    public Disconnect() {
    }

    public Disconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void read(ByteBuf buf) {
        reason = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(reason, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "Disconnect{" +
                "reason='" + reason + '\'' +
                '}';
    }

    public String getReason() {
        return reason;
    }
}

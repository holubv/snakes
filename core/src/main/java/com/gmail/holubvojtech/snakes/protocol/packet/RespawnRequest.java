package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Serverbound
public class RespawnRequest extends DefinedPacket {

    @Override
    public void read(ByteBuf buf) {
    }

    @Override
    public void write(ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "RespawnRequest{}";
    }
}

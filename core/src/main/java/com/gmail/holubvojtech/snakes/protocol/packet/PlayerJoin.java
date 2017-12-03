package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class PlayerJoin extends DefinedPacket {

    private int playerId;
    private String name;

    public PlayerJoin() {
    }

    public PlayerJoin(int playerId, String name) {
        this.playerId = playerId;
        this.name = name;
    }

    @Override
    public void read(ByteBuf buf) {
        playerId = buf.readUnsignedShort();
        name = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeShort(playerId);
        writeString(name, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "PlayerJoin{" +
                "playerId=" + playerId +
                ", name='" + name + '\'' +
                '}';
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }
}

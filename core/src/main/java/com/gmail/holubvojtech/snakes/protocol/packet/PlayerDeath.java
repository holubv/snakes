package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class PlayerDeath extends DefinedPacket {

    private int playerId;

    public PlayerDeath() {
    }

    public PlayerDeath(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void read(ByteBuf buf) {
        playerId = buf.readUnsignedShort();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeShort(playerId);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "PlayerDeath{" +
                "playerId=" + playerId +
                '}';
    }

    public int getPlayerId() {
        return playerId;
    }
}

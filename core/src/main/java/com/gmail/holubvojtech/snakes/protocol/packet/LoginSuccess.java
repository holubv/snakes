package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class LoginSuccess extends DefinedPacket {

    private int playerId;

    public LoginSuccess() {
    }

    public LoginSuccess(int playerId) {
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
        return "LoginSuccess{" +
                "playerId=" + playerId +
                '}';
    }

    public int getPlayerId() {
        return playerId;
    }
}

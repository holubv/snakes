package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import io.netty.buffer.ByteBuf;

@Serverbound
public class Handshake extends DefinedPacket {

    private int protocolVersion;
    private boolean login;

    public Handshake() {
    }

    public Handshake(boolean login) {
        this.protocolVersion = Protocol.VERSION;
        this.login = login;
    }

    public Handshake(int protocolVersion, boolean login) {
        this.protocolVersion = protocolVersion;
        this.login = login;
    }

    @Override
    public void read(ByteBuf buf) {
        protocolVersion = buf.readUnsignedByte();
        login = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(protocolVersion);
        buf.writeBoolean(login);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "Handshake{" +
                "protocolVersion=" + protocolVersion +
                ", login=" + login +
                '}';
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public boolean isLogin() {
        return login;
    }
}

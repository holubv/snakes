package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import io.netty.buffer.ByteBuf;

@Clientbound
public class ServerStatus extends DefinedPacket {

    private int protocolVersion;
    private int players;
    private int maxPlayers;
    private String description;

    public ServerStatus() {
    }

    public ServerStatus(int players, int maxPlayers, String description) {
        this.protocolVersion = Protocol.VERSION;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.description = description;
    }

    public ServerStatus(int protocolVersion, int players, int maxPlayers, String description) {
        this.protocolVersion = protocolVersion;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.description = description;
    }

    @Override
    public void read(ByteBuf buf) {
        protocolVersion = buf.readUnsignedByte();
        players = buf.readUnsignedShort();
        maxPlayers = buf.readUnsignedShort();
        description = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(protocolVersion);
        buf.writeShort(players);
        buf.writeShort(maxPlayers);
        writeString(description, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "protocolVersion=" + protocolVersion +
                ", players=" + players +
                ", maxPlayers=" + maxPlayers +
                ", description='" + description + '\'' +
                '}';
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDescription() {
        return description;
    }
}

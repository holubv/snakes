package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class SnakeTailSizeChange extends DefinedPacket {

    private int entityId;
    private int mod;

    public SnakeTailSizeChange() {
    }

    public SnakeTailSizeChange(int entityId, int mod) {
        this.entityId = entityId;
        this.mod = mod;
    }

    public SnakeTailSizeChange(SnakeEntity entity, int mod) {
        this.entityId = entity.getEntityId();
        this.mod = mod;
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = (int) buf.readUnsignedInt();
        this.mod = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(mod);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public int getMod() {
        return mod;
    }

    @Override
    public String toString() {
        return "SnakeTailSizeChange{" +
                "entityId=" + entityId +
                ", mod=" + mod +
                '}';
    }
}

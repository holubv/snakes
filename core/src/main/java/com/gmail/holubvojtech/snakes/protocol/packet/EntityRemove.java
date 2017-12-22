package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class EntityRemove extends DefinedPacket {

    private int entityId;

    public EntityRemove() {
    }

    public EntityRemove(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = (int) buf.readUnsignedInt();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return "EntityRemove{" +
                "entityId=" + entityId +
                '}';
    }
}

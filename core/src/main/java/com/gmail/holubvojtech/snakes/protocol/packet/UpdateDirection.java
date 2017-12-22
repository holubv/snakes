package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
@Serverbound
public class UpdateDirection extends DefinedPacket {

    private int entityId;
    private Direction direction;
    private Coords coords;

    public UpdateDirection() {
    }

    public UpdateDirection(int entityId, Direction direction, Coords coords) {
        this.entityId = entityId;
        this.direction = direction;
        this.coords = coords;
    }

    @Override
    public void read(ByteBuf buf) {
        entityId = (int) buf.readUnsignedInt();
        direction = Direction.values()[buf.readUnsignedByte()];
        coords = readCoords(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(direction.ordinal());
        writeCoords(coords, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public Direction getDirection() {
        return direction;
    }

    public Coords getCoords() {
        return coords;
    }

    @Override
    public String toString() {
        return "UpdateDirection{" +
                "entityId=" + entityId +
                ", direction=" + direction +
                ", coords=" + coords +
                '}';
    }
}

package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.EntityType;
import com.gmail.holubvojtech.snakes.entity.FoodEntity;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

@Clientbound
public class EntitySpawn extends DefinedPacket {

    private int entityId;
    private EntityType entityType;
    private Coords coords;

    private Entity entity;

    public EntitySpawn() {
    }

    public EntitySpawn(Entity entity) {
        this.entityId = entity.getEntityId();
        this.entityType = entity.getType();
        this.coords = entity.getCoords();
        this.entity = entity;
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = (int) buf.readUnsignedInt();
        this.entityType = EntityType.values()[buf.readUnsignedByte()];
        this.coords = readCoords(buf);

        //todo better metadata handling

        //metadata
        if (entityType == EntityType.SNAKE) {

            SnakeEntity entity = new SnakeEntity(entityId, coords);
            entity.setPlayerId(buf.readUnsignedShort());
            entity.setSpeed(buf.readDouble());
            entity.setColor(readColor(buf));
            this.entity = entity;
            return;
        }

        if (entityType == EntityType.FOOD) {
            this.entity = new FoodEntity(entityId, coords, FoodEntity.Type.values()[buf.readUnsignedByte()]);
            return;
        }

        throw new DecoderException("unsupported entity type");
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(entityType.ordinal());
        writeCoords(coords, buf);

        //todo better metadata handling

        //metadata
        if (entityType == EntityType.SNAKE) {

            SnakeEntity entity = (SnakeEntity) this.entity;
            buf.writeShort(entity.getPlayerId());
            buf.writeDouble(entity.getSpeed());
            writeColor(entity.getColor(), buf);
            return;
        }

        if (entityType == EntityType.FOOD) {

            FoodEntity entity = (FoodEntity) this.entity;
            buf.writeByte(entity.getFoodType().ordinal());
            return;
        }

        throw new EncoderException("unsupported entity type");
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Coords getCoords() {
        return coords;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "EntitySpawn{" +
                "entityId=" + entityId +
                ", entityType=" + entityType +
                ", coords=" + coords +
                ", entity=" + entity +
                '}';
    }
}

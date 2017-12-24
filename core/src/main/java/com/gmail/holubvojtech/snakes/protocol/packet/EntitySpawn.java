package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.Color;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
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

    private Metadata metadata;
    private Entity entity;

    public EntitySpawn() {
    }

    public EntitySpawn(Entity entity) {
        this.entityId = entity.getEntityId();
        this.entityType = entity.getType();
        this.coords = entity.getCoords();

        if (entity.getType() == EntityType.SNAKE) {
            metadata = new SnakeMetadata((SnakeEntity) entity);
        } else if (entity.getType() == EntityType.FOOD) {
            metadata = new FoodMetadata((FoodEntity) entity);
        }
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = (int) buf.readUnsignedInt();
        this.entityType = EntityType.values()[buf.readUnsignedByte()];
        this.coords = readCoords(buf);

        //todo better metadata handling

        //metadata
        if (entityType == EntityType.SNAKE) {
            entity = new SnakeEntity(entityId, coords);
            SnakeMetadata meta = new SnakeMetadata();
            meta.read(buf);
            meta.apply(entity);
            return;
        }

        if (entityType == EntityType.FOOD) {
            entity = new FoodEntity(entityId, coords);
            FoodMetadata meta = new FoodMetadata();
            meta.read(buf);
            meta.apply(entity);
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
        if (metadata == null) {
            throw new EncoderException("unsupported entity type");
        }
        metadata.write(buf);
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

    private interface Metadata {
        void read(ByteBuf buf);

        void write(ByteBuf buf);

        void apply(Entity entity);
    }

    private static class SnakeMetadata implements Metadata {

        private int playerId;
        private Direction direction;
        private double speed;
        private Color color;

        public SnakeMetadata() {
        }

        public SnakeMetadata(SnakeEntity entity) {
            playerId = entity.getPlayerId();
            direction = entity.getDirection();
            speed = entity.getSpeed();
            color = entity.getColor();
        }

        @Override
        public void read(ByteBuf buf) {
            playerId = buf.readUnsignedShort();
            direction = Direction.values()[buf.readUnsignedByte()];
            speed = buf.readDouble();
            color = readColor(buf);
        }

        @Override
        public void write(ByteBuf buf) {
            buf.writeShort(playerId);
            buf.writeByte(direction.ordinal());
            buf.writeDouble(speed);
            writeColor(color, buf);
        }

        @Override
        public void apply(Entity entity) {
            SnakeEntity snake = (SnakeEntity) entity;
            snake.setPlayerId(playerId);
            snake.__instantGigaMegaChangeDirectionWithoutWaitingForAnything(direction);
            snake.setSpeed(speed);
            snake.setColor(color);
        }
    }

    private static class FoodMetadata implements Metadata {

        private FoodEntity.Type foodType;

        public FoodMetadata() {
        }

        public FoodMetadata(FoodEntity entity) {
            foodType = entity.getFoodType();
        }

        @Override
        public void read(ByteBuf buf) {
            foodType = FoodEntity.Type.values()[buf.readUnsignedByte()];
        }

        @Override
        public void write(ByteBuf buf) {
            buf.writeByte(foodType.ordinal());
        }

        @Override
        public void apply(Entity entity) {
            FoodEntity food = (FoodEntity) entity;
            food.setType(foodType);
        }
    }
}

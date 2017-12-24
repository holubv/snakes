package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Clientbound
public class SnakeTail extends DefinedPacket {

    private static final byte[] DIR_MASK = new byte[]{
            0b00, //up
            0b11, //down
            0b10, //left
            0b01  //right
    };

    private int entityId;
    private Direction direction;
    private Direction[] tail;

    public SnakeTail() {
    }

    public SnakeTail(SnakeEntity entity) {
        this.entityId = entity.getEntityId();
        this.direction = entity.getDirection();

        List<Direction> tail = entity.getTail();
        this.tail = tail.toArray(new Direction[tail.size()]);
    }

    public SnakeTail(int entityId, Direction direction, Collection<Direction> tail) {
        this.entityId = entityId;
        this.direction = direction;
        this.tail = tail.toArray(new Direction[tail.size()]);
    }

    public static Direction[] readSnakeTail(ByteBuf buf) {
        Direction[] tail = new Direction[buf.readUnsignedShort()];

        byte[] buff = new byte[(int) Math.ceil(tail.length / 4.0)];
        buf.readBytes(buff);
        int bp = 0;
        for (int tp = 0; tp < tail.length; tp++) {
            int off = tp % 4;
            if (tp != 0 && off == 0) {
                bp++;
            }
            byte val = (byte) (buff[bp] >> (off * 2) & 0b11);
            switch (val) {
                case 0b00:
                    tail[tp] = Direction.UP;
                    break;
                case 0b11:
                    tail[tp] = Direction.DOWN;
                    break;
                case 0b10:
                    tail[tp] = Direction.LEFT;
                    break;
                case 0b01:
                    tail[tp] = Direction.RIGHT;
                    break;
                default:
                    throw new DecoderException();
            }
        }

        return tail;
    }

    public static void writeSnakeTail(ByteBuf buf, Direction[] tail) {
        buf.writeShort(tail.length);

        byte[] buff = new byte[(int) Math.ceil(tail.length / 4.0)];
        int bp = 0;
        for (int tp = 0; tp < tail.length; tp++) {
            int off = tp % 4;
            if (tp != 0 && off == 0) {
                bp++;
            }
            byte mask = DIR_MASK[tail[tp].ordinal()];
            buff[bp] |= (mask << (off * 2)) & 0xff;
        }

        buf.writeBytes(buff);
    }

    @Override
    public void read(ByteBuf buf) {
        this.entityId = (int) buf.readUnsignedInt();
        this.direction = Direction.values()[buf.readUnsignedByte()];
        this.tail = readSnakeTail(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(direction.ordinal());
        writeSnakeTail(buf, tail);
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

    public Direction[] getTail() {
        return tail;
    }

    public List<Direction> getTailAsList() {
        return Arrays.asList(tail);
    }

    @Override
    public String toString() {
        return "SnakeTail{" +
                "entityId=" + entityId +
                ", direction=" + direction +
                ", tail=" + Arrays.toString(tail) +
                '}';
    }
}

package com.gmail.holubvojtech.snakes.protocol.packet;

import com.gmail.holubvojtech.snakes.AxisAlignedBB;
import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import io.netty.buffer.ByteBuf;

@Clientbound
public class MapData extends DefinedPacket {

    private AxisAlignedBB mapBounds;

    public MapData() {
    }

    public MapData(AxisAlignedBB mapBounds) {
        this.mapBounds = new AxisAlignedBB(
                mapBounds.getCoords().blockCoords(),
                Math.floor(mapBounds.getWidth()),
                Math.floor(mapBounds.getHeight())
        );
    }

    @Override
    public void read(ByteBuf buf) {
        boolean hasBounds = buf.readBoolean();
        if (hasBounds) {
            int x = buf.readShort();
            int y = buf.readShort();
            int w = buf.readUnsignedShort();
            int h = buf.readUnsignedShort();
            mapBounds = new AxisAlignedBB(x, y, w, h);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBoolean(mapBounds != null);
        if (mapBounds != null) {
            buf.writeShort((int) mapBounds.getX());
            buf.writeShort((int) mapBounds.getY());
            buf.writeShort((int) mapBounds.getWidth());
            buf.writeShort((int) mapBounds.getHeight());
        }
    }

    public AxisAlignedBB getMapBounds() {
        return mapBounds;
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "MapData{" +
                "mapBounds=" + mapBounds +
                '}';
    }
}

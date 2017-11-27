package com.gmail.holubvojtech.snakes.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private Protocol protocol;

    public PacketDecoder(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        ByteBuf copy = in.copy();
        int packetId = in.readUnsignedByte();
        DefinedPacket packet;
        if (protocol.hasPacket(packetId)) {
            packet = protocol.createPacket(packetId);
            packet.read(in);
            if (in.readableBytes() != 0) {
                throw new RuntimeException("Did not read all bytes from packet " + packet.getClass() + " " + packetId);
            }
        } else {
            //in.skipBytes(in.readableBytes());
            throw new RuntimeException("Unknown packet id " + packetId);
        }

        out.add(new PacketWrapper(packet, copy));
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}

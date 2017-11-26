package com.gmail.holubvojtech.snakes.protocol;

import io.netty.buffer.ByteBuf;

public class PacketWrapper {

    private final DefinedPacket packet;
    private final ByteBuf buf;
    private boolean released;

    public PacketWrapper(DefinedPacket packet, ByteBuf buf) {
        this.packet = packet;
        this.buf = buf;
    }

    public void trySingleRelease() {
        if (!this.released) {
            this.buf.release();
            this.released = true;
        }
    }

    public DefinedPacket getPacket() {
        return packet;
    }

    public ByteBuf getBuf() {
        return buf;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }
}

package com.gmail.holubvojtech.snakes.netty;

import com.gmail.holubvojtech.snakes.protocol.AbstractPacketHandler;

public abstract class PacketHandler extends AbstractPacketHandler {

    public void exception(Throwable t) throws Exception {
    }

    public void connected(ChannelWrapper channel) throws Exception {
    }

    public void disconnected(ChannelWrapper channel) throws Exception {
    }
}

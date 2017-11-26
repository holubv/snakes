package com.gmail.holubvojtech.snakes.netty;

import com.gmail.holubvojtech.snakes.protocol.PacketWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class ChannelWrapper {

    private final Channel ch;
    private volatile boolean closed;

    public ChannelWrapper(Channel ch) {
        this.ch = ch;
    }

    public ChannelWrapper(ChannelHandlerContext ctx) {
        this.ch = ctx.channel();
    }

    public void write(Object packet) {

        if (!closed) {

            if (packet instanceof PacketWrapper) {

                PacketWrapper wrap = (PacketWrapper) packet;

                wrap.setReleased(true);
                ch.write(wrap.getBuf(), ch.voidPromise());

            } else {
                ch.write(packet, ch.voidPromise());
            }

            this.ch.flush();
        }
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {

        if (ch.eventLoop().inEventLoop()) {
            throw new IllegalStateException("cannot add handler outside of event loop");
        }

        ch.pipeline().flush();
        ch.pipeline().addBefore(baseName, name, handler);
    }

    public void close() {
        if (!closed) {
            closed = true;
            ch.flush();
            ch.close();
        }
    }

    public Channel getHandle() {
        return this.ch;
    }

    public boolean isClosed() {
        return this.closed;
    }
}

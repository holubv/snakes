package com.gmail.holubvojtech.snakes.netty;

import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.PacketWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HandlerBoss extends ChannelInboundHandlerAdapter {

    private ChannelWrapper channel;
    private PacketHandler handler;

    public HandlerBoss() {
    }

    public void setHandler(PacketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            channel = new ChannelWrapper(ctx);
            handler.connected(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (handler != null) {
            handler.disconnected(channel);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.handler != null) {
            PacketWrapper packet = (PacketWrapper) msg;

            try {

                DefinedPacket dp = packet.getPacket();

                if (dp != null) {
                    /*ctx.channel().eventLoop().schedule(() -> {
                        try {
                            dp.handle(this.handler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 50, TimeUnit.MILLISECONDS);*/
                    dp.handle(this.handler);
                }

            } finally {
                packet.trySingleRelease();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {

            if (handler != null) {
                try {
                    handler.exception(cause);
                } catch (Exception e) {
                    System.out.println("Exception in exception handler: " + e.getMessage());
                }
            }

            ctx.close();
        }
    }
}

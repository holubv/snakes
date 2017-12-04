package com.gmail.holubvojtech.snakes.server;

import com.gmail.holubvojtech.snakes.Utils;
import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.Connection;
import com.gmail.holubvojtech.snakes.netty.PacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ClientConnection extends PacketHandler implements Connection {

    private SnakesServer server;

    private ChannelWrapper ch;
    private final Unsafe unsafe = new Unsafe() {
        @Override
        public void sendPacket(DefinedPacket packet) {
            ch.write(packet);
        }
    };

    public ClientConnection(SnakesServer server) {
        this.server = server;
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) ch.getHandle().remoteAddress();
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
        this.server.addConnection(this);
    }

    @Override
    public void exception(Throwable t) throws Exception {
        System.out.println(this.toString() + ": Exception in ChannelAdapter: " + Utils.exception(t));
        disconnect();
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        server.removeConnection(this);
    }

    @Override
    public void disconnect() {

        if (!ch.isClosed()) {

            System.out.println("Client " + this.toString() + " was disconnected");

            this.ch.getHandle().eventLoop().schedule(() -> {
                //todo send some kick packet
                ch.close();
            }, 20L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }
}

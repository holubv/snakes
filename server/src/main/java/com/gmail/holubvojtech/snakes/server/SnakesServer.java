package com.gmail.holubvojtech.snakes.server;

import com.gmail.holubvojtech.snakes.netty.HandlerBoss;
import com.gmail.holubvojtech.snakes.netty.PipelineUtils;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.PacketDecoder;
import com.gmail.holubvojtech.snakes.protocol.PacketEncoder;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SnakesServer {

    private boolean running = false;
    private int port;

    private Set<Channel> channels = new HashSet<>();
    private EventLoopGroup eventLoops;
    private ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    private Set<ClientConnection> connections = new HashSet<>(100);

    public SnakesServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {

        this.eventLoops = PipelineUtils.newEventLoopGroup();

        running = true;

        new ServerBootstrap()
                .channel(PipelineUtils.getServerChannel())
                .group(eventLoops)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {

                        PipelineUtils.BASE.initChannel(ch);

                        ch.pipeline().addAfter("frame-decoder", "packet-decoder", new PacketDecoder(Protocol.SERVER_BOUND));
                        ch.pipeline().addAfter("frame-prepender", "packet-encoder", new PacketEncoder(Protocol.CLIENT_BOUND));

                        ch.pipeline().get(HandlerBoss.class).setHandler(new ClientConnection(SnakesServer.this));
                    }
                })
                .bind(port)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        channels.add(future.channel());
                        System.out.println("Listening on " + port);
                    } else {
                        System.out.println("Cannot bind to the port " + port + ": " + future.cause().getMessage());
                    }
                });
    }

    private void closeChannels() {

        for (Channel channel : this.channels) {

            System.out.println("Closing channel " + channel);

            try {
                channel.close().syncUninterruptibly();
            } catch (ChannelException var4) {
                System.out.println("Could not close channel");
            }
        }

        this.channels.clear();
    }

    public void stop() {

        new Thread("Shutdown thread") {

            @Override
            public void run() {

                running = false;

                closeChannels();

                System.out.println("Closing pending connections");
                connectionLock.readLock().lock();

                try {

                    System.out.println("Disconnecting " + connections.size() + " connections");

                    for (ClientConnection conn : connections) {
                        conn.disconnect();
                    }

                } finally {
                    connectionLock.readLock().unlock();
                }

                System.out.println("Closing IO threads");

                eventLoops.shutdownGracefully();

                try {
                    eventLoops.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException ignored) {
                }

                System.out.println("server shut down");
            }

        }.start();
    }

    public void broadcast(DefinedPacket packet) {
        broadcast(packet, null);
    }

    public void broadcast(DefinedPacket packet, ClientConnection except) {

        connectionLock.readLock().lock();

        try {

            for (ClientConnection conn : connections) {

                if (conn.equals(except)) {
                    continue;
                }

                conn.unsafe().sendPacket(packet);
            }

        } finally {
            connectionLock.readLock().unlock();
        }
    }

    public void addConnection(ClientConnection connection) {

        System.out.println(connection.toString() + " connected");

        connectionLock.writeLock().lock();

        try {
            connections.add(connection);
        } finally {
            connectionLock.writeLock().unlock();
        }
    }

    public void removeConnection(ClientConnection connection) {

        System.out.println(connection.toString() + " disconnected");

        connectionLock.writeLock().lock();

        try {
            connections.remove(connection);
        } finally {
            connectionLock.writeLock().unlock();
        }
    }

    public boolean isRunning() {
        return running;
    }
}

package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.Connection;
import com.gmail.holubvojtech.snakes.netty.HandlerBoss;
import com.gmail.holubvojtech.snakes.netty.PipelineUtils;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.PacketDecoder;
import com.gmail.holubvojtech.snakes.protocol.PacketEncoder;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SnakesClient implements Connection {

    private boolean running = false;
    private InetSocketAddress address;
    private Channel channel;
    private EventLoopGroup eventLoops;

    private ChannelWrapper ch;
    private final Unsafe unsafe = new Unsafe() {
        @Override
        public void sendPacket(DefinedPacket packet) {
            ch.write(packet);
        }
    };

    private Snakes snakes;

    public SnakesClient(Snakes snakes, InetSocketAddress address) {
        this.snakes = snakes;
        this.address = address;
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("Client was already started");
        }
        eventLoops = PipelineUtils.newEventLoopGroup();
        running = true;
    }

    public void connect(Consumer<Throwable> callback) {

        if (!running) {
            throw new IllegalStateException("Client is not running");
        }

        new Bootstrap()
                .channel(PipelineUtils.getChannel())
                .group(eventLoops)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {

                        PipelineUtils.BASE.initChannel(ch);

                        ch.pipeline().addAfter("frame-decoder", "packet-decoder", new PacketDecoder(Protocol.CLIENT_BOUND));
                        ch.pipeline().addAfter("frame-prepender", "packet-encoder", new PacketEncoder(Protocol.SERVER_BOUND));

                        ch.pipeline().get(HandlerBoss.class).setHandler(SnakesClient.this.snakes);
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .remoteAddress(address)
                .connect()
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        callback.accept(null);
                    } else {
                        future.channel().close();
                        future.cause().printStackTrace();
                        callback.accept(future.cause());
                    }
                });
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) ch.getHandle().remoteAddress();
    }

    @Override
    public void disconnect() {
        if (!ch.isClosed()) {
            this.ch.getHandle().eventLoop().schedule(() -> ch.close(), 1L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }

    public void stop() {

        new Thread("Shutdown thread") {
            @Override
            public void run() {
                stopSync();
            }
        }.start();
    }

    public boolean stopSync() {
        return stopSync(Long.MAX_VALUE);
    }

    public void closeChannelSync() {

        if (channel == null) {
            return;
        }

        channel.close().syncUninterruptibly();
    }

    public boolean stopSync(long timeout) {

        running = false;
        closeChannelSync();
        eventLoops.shutdownGracefully();

        try {
            eventLoops.awaitTermination(timeout, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    public void setChannel(ChannelWrapper ch) {
        this.ch = ch;
    }
}

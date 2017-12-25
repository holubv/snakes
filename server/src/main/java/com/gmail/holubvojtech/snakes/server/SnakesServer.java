package com.gmail.holubvojtech.snakes.server;

import com.gmail.holubvojtech.snakes.*;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.EntityType;
import com.gmail.holubvojtech.snakes.entity.FoodEntity;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.netty.HandlerBoss;
import com.gmail.holubvojtech.snakes.netty.PipelineUtils;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.PacketDecoder;
import com.gmail.holubvojtech.snakes.protocol.PacketEncoder;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import com.gmail.holubvojtech.snakes.protocol.packet.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class SnakesServer {

    private static final int TARGET_TPS = 60;
    private static final long NANOS_PER_TICK = 1000_000_000L / TARGET_TPS;

    private static final Color[] SNAKE_COLORS = new Color[]{
            new Color(0xF44336),
            new Color(0xE91E63),
            new Color(0x9C27B0),
            new Color(0x673AB7),
            new Color(0x3F51B5),
            new Color(0x2196F3),
            new Color(0x03A9F4),
            new Color(0x00BCD4),
            new Color(0x009688),
            new Color(0x4CAF50),
            new Color(0x8BC34A),
            new Color(0xCDDC39),
            new Color(0xFFEB3B),
            new Color(0xFFC107),
            new Color(0xFF9800),
            new Color(0xFF5722),
            new Color(0x795548),
            new Color(0x607D8B)
    };

    private boolean running = false;
    private int port;

    private Set<Channel> channels = new HashSet<>();
    private EventLoopGroup eventLoops;
    private ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    private Set<ClientConnection> connections = new HashSet<>(100);

    public SnakesServer(int port) {
        this.port = port;
    }

    public volatile int currentTick = 0;
    private Thread tickThread;
    private long lastTime;
    private int ticks = 0;
    private double tps = TARGET_TPS;
    private Queue<Runnable> scheduled = new ConcurrentLinkedQueue<>();

    private AxisAlignedBB mapBounds/* = new AxisAlignedBB(-50, -50, 100, 100)*/;
    private List<Entity> entities = new ArrayList<>();

    public void start() throws InterruptedException {

        this.eventLoops = PipelineUtils.newEventLoopGroup();

        running = true;

        tickThread = new Thread(() -> {
            try {

                long lastTick = System.nanoTime();
                long catchupTime = 0L;
                long tickSection = lastTick;

                long time;
                lastTime = System.currentTimeMillis();

                while (running) {
                    long curTime = System.nanoTime();
                    long wait = NANOS_PER_TICK - (curTime - lastTick) - catchupTime;
                    if (wait > 0L) {
                        Thread.sleep(wait / 1000000L);
                        catchupTime = 0L;
                    } else {
                        catchupTime = Math.min(1000000000L, Math.abs(wait)); //1s
                        if (currentTick++ % 100 == 0) {
                            tps = 1.0E9D / (double) (curTime - tickSection) * 100.0D;
                            tickSection = curTime;
                        }

                        lastTick = curTime;
                        time = System.currentTimeMillis();
                        tick((int) (time - lastTime));
                        lastTime = time;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, "ServerTickThread");
        tickThread.start();

        new ServerBootstrap()
                .channel(PipelineUtils.getServerChannel())
                .group(eventLoops)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
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

    public void schedule(Runnable r) {
        this.scheduled.offer(r);
    }

    public void tick(int delta) {

        while (!scheduled.isEmpty()) {
            scheduled.poll().run();
        }

        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it.next();
            if (entity.isRemoved()) {
                broadcast(new EntityRemove(entity.getEntityId()));
                it.remove();
                continue;
            }

            entity.update(delta);
        }

        //this is very basic collision system
        //problems can occur when server/game lags
        //or when entity moves very fast
        for (Entity e1 : entities) {

            if (mapBounds != null && e1 instanceof SnakeEntity) {
                if (!mapBounds.contains(e1.getBoundingBox())) {
                    System.out.println("### out of map ###");
                }
            }

            for (Entity e2 : entities) {
                if (!e1.isRemoved() && !e2.isRemoved() && Entity.collides(e1, e2)) {
                    if (e1 instanceof SnakeEntity && e2.getType() == EntityType.FOOD) {
                        e2.remove();
                        SnakeEntity snake = (SnakeEntity) e1;
                        FoodEntity food = (FoodEntity) e2;
                        if (food.getFoodType() == FoodEntity.Type.GROW) {
                            snake.grow();
                            broadcast(new SnakeTailSizeChange((SnakeEntity) e1, 1));
                        } else {
                            snake.shrink();
                            broadcast(new SnakeTailSizeChange((SnakeEntity) e1, -1));
                        }

                        System.out.println("snake ate food");
                    }
                    if (e2 instanceof SnakeEntity && e1 instanceof SnakeEntity) {
                        System.out.println("### COLLISION ###");
                    }
                }
            }
        }

        if (currentTick % 100 == 0) {
            spawnEntity(new FoodEntity(new Coords(Utils.randomInt(-40, 40), Utils.randomInt(-40, 40))));
        }
    }

    public Entity getEntity(int entityId) {
        for (Entity entity : entities) {
            if (entity.getEntityId() == entityId) {
                return entity;
            }
        }
        return null;
    }

    public Entity spawnEntity(Entity entity) {
        entities.add(entity);
        broadcast(new EntitySpawn(entity));
        if (entity instanceof SnakeEntity) {
            broadcast(new SnakeTail((SnakeEntity) entity));
        }
        return entity;
    }

    public void removeEntity(int entityId) {
        Entity entity = getEntity(entityId);
        if (entity != null) {
            entity.remove();
        }
    }

    public ClientConnection getClientBySnake(int snakeId) {
        return getClient(client -> client.getSnakeId() == snakeId);
    }

    public void onPlayerConnected(ClientConnection connection) {
        schedule(() -> {

            if (mapBounds != null) {
                connection.unsafe().sendPacket(new MapData(mapBounds));
            }

            for (Entity entity : entities) {
                connection.unsafe().sendPacket(new EntitySpawn(entity));
                if (entity instanceof SnakeEntity) {
                    connection.unsafe().sendPacket(new SnakeTail((SnakeEntity) entity));
                }
            }

            SnakeEntity entity = new SnakeEntity(new Coords());
            entity.setPlayerId(connection.getPlayerId());
            entity.getTail().add(Direction.UP);
            entity.getTail().add(Direction.UP);
            entity.getTail().add(Direction.UP);
            entity.getTail().add(Direction.UP);
            entity.getTail().add(Direction.UP);
            entity.setColor(Utils.randomValue(SNAKE_COLORS));
            connection.setSnakeId(entity.getEntityId());
            spawnEntity(entity);
        });
        broadcast(new PlayerJoin(connection.getPlayerId(), connection.getUsername()), connection);
    }

    public void onPlayerDisconnected(ClientConnection connection) {
        schedule(() -> removeEntity(connection.getSnakeId()));
        broadcast(new PlayerLeave(connection.getPlayerId()), connection);
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

    public void forClient(Consumer<ClientConnection> callback) {
        connectionLock.readLock().lock();
        try {
            for (ClientConnection connection : connections) {
                callback.accept(connection);
            }
        } finally {
            connectionLock.readLock().unlock();
        }
    }

    public ClientConnection getClient(Function<ClientConnection, Boolean> accept) {
        connectionLock.readLock().lock();
        try {
            for (ClientConnection connection : connections) {
                if (accept.apply(connection)) {
                    return connection;
                }
            }
        } finally {
            connectionLock.readLock().unlock();
        }
        return null;
    }

    public void __addConnection(ClientConnection connection) {

        System.out.println(connection.toString() + " connected");

        connectionLock.writeLock().lock();

        try {
            connections.add(connection);
        } finally {
            connectionLock.writeLock().unlock();
        }
    }

    public void __removeConnection(ClientConnection connection) {

        System.out.println(connection.toString() + " disconnected");

        onPlayerDisconnected(connection);

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

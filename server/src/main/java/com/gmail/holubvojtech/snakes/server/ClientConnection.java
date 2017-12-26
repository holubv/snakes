package com.gmail.holubvojtech.snakes.server;

import com.gmail.holubvojtech.snakes.Color;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.Utils;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.Connection;
import com.gmail.holubvojtech.snakes.netty.PacketHandler;
import com.gmail.holubvojtech.snakes.protocol.DefinedPacket;
import com.gmail.holubvojtech.snakes.protocol.Protocol;
import com.gmail.holubvojtech.snakes.protocol.packet.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientConnection extends PacketHandler implements Connection {

    private static final AtomicInteger NEXT_PLAYER_ID = new AtomicInteger(1);

    private SnakesServer server;

    private int playerId;
    private int snakeId;
    private String username;
    private String version;
    private boolean handshake;
    private boolean playState;
    private boolean wantRespawn;
    private Color snakeColor = Utils.randomValue(SnakesServer.SNAKE_COLORS);

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
    public void handle(RespawnRequest packet) throws Exception {
        if (!playState || wantRespawn || snakeId > 0) {
            return;
        }
        wantRespawn = true;
        this.server.schedule(() -> {
            SnakeEntity entity = new SnakeEntity(new Coords());
            entity.setPlayerId(playerId);
            entity.getTail().add(Direction.UP);
            entity.setColor(snakeColor);

            setSnakeId(entity.getEntityId());
            server.spawnEntity(entity);
            wantRespawn = false;
        });
    }

    @Override
    public void handle(UpdateDirection packet) throws Exception {
        if (!playState) {
            return;
        }
        this.server.broadcast(packet, this);
        this.server.schedule(() -> {
            SnakeEntity entity = (SnakeEntity) server.getEntity(snakeId);
            if (entity != null) {
                entity.updateDirection(packet.getDirection(), packet.getCoords());
            }
        });
    }

    @Override
    public void handle(Handshake handshake) throws Exception {
        if (!handshake.isLogin()) {
            //todo send server status
            disconnect();
            return;
        }

        if (handshake.getProtocolVersion() != Protocol.VERSION) {
            //todo send reason?
            disconnect();
            return;
        }

        this.handshake = true;
    }

    @Override
    public void handle(Login login) throws Exception {
        if (playState) {
            throw new IllegalStateException("already logged in");
        }
        this.username = login.getUsername();
        if (!Utils.validateUsername(username)) {
            throw new IllegalArgumentException("invalid nickname");
        }
        this.version = login.getVersion();
        this.playerId = NEXT_PLAYER_ID.getAndIncrement();

        unsafe().sendPacket(new LoginSuccess(playerId));
        playState = true;
        System.out.println("Player " + username + "(" + playerId + ") connected to the server");
        this.server.onPlayerConnected(this);
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getSnakeId() {
        return snakeId;
    }

    public ClientConnection setSnakeId(int snakeId) {
        this.snakeId = snakeId;
        return this;
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
        this.server.__addConnection(this);
    }

    @Override
    public void exception(Throwable t) throws Exception {
        System.out.println(this.toString() + ": Exception in ChannelAdapter: " + Utils.exception(t));
        disconnect();
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        server.__removeConnection(this);
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

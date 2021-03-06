package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.AxisAlignedBB;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.Utils;
import com.gmail.holubvojtech.snakes.client.gui.*;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.EntityType;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.PacketHandler;
import com.gmail.holubvojtech.snakes.protocol.packet.*;
import com.gmail.holubvojtech.snakes.server.SnakesServer;
import org.newdawn.slick.*;
import org.newdawn.slick.util.InputAdapter;
import org.newdawn.slick.util.ResourceLoader;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Snakes extends PacketHandler implements Game {

    public static final String VERSION = "0.1";

    public static Snakes inst;
    public static Font font;
    public static Font chatFont;

    private GameContainer container;
    private Gui gui;
    private boolean showFps = true;

    private GameOverlay overlay;
    private InputField nameField;

    private boolean running;
    private GameRenderer renderer;

    private SnakesServer localServer;

    private boolean connecting;
    private boolean connected;
    private SnakesClient client;
    private Queue<Runnable> scheduled = new ConcurrentLinkedQueue<>();

    private int playerId;
    private SnakeEntity playerSnake;
    private AxisAlignedBB mapBounds = null;
    private Map<Integer, String> players = new ConcurrentHashMap<>();
    private List<Entity> entities = new ArrayList<>();

    private long lastDirectionChange;

    @Override
    public void init(GameContainer container) throws SlickException {
        inst = this;
        this.container = container;
        container.setShowFPS(false);
        container.setTargetFrameRate(60);
        container.setAlwaysRender(true);

        try {
            java.awt.Font f = java.awt.Font.createFont(0, ResourceLoader.getResourceAsStream("kongtext.ttf"));
            font = new TrueTypeFont(
                    f.deriveFont(16f),
                    false
            );
            chatFont = new TrueTypeFont(
                    f.deriveFont(12f),
                    true
            );
        } catch (Exception e) {
            throw new SlickException("cannot load font", e);
        }

        gui = new Gui(container.getInput());
        MainMenu mm = new MainMenu(container);
        this.nameField = mm.getNameField();
        gui.savePanel("main", mm);
        gui.savePanel("createServer", new CreateServerMenu(container));
        gui.savePanel("connect", new ConnectMenu(container));
        //todo gui.savePanel("overlay", overlay = new GameOverlay(container));
        gui.setRoot("main");

        Camera camera = new Camera(new Coords(), container.getWidth(), container.getHeight());
        camera.coords.setX(-camera.width / 2.0).setY(-camera.height / 2.0);
        renderer = new GameRenderer(this, camera);

        container.getInput().addMouseListener(new InputAdapter() {
            @Override
            public void mouseWheelMoved(int change) {
                if (!running) {
                    return;
                }
                Camera camera = renderer.getCamera();
                if (change > 0 && camera.size < 32) {
                    camera.size += 2;
                } else if (change < 0 && camera.size > 4) {
                    camera.size -= 2;
                }
            }
        });

        container.getInput().addKeyListener(new InputAdapter() {
            @Override
            public void keyPressed(int key, char c) {
                if (playerSnake == null) {
                    if (running && key == Input.KEY_ENTER) {
                        client.unsafe().sendPacket(new RespawnRequest());
                    }
                    return;
                }
                if (key == Input.KEY_LEFT) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        playerSnake.setDirection(Direction.LEFT);
                    } else {
                        playerSnake.enqueueDirection(Direction.LEFT);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_RIGHT) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        playerSnake.setDirection(Direction.RIGHT);
                    } else {
                        playerSnake.enqueueDirection(Direction.RIGHT);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_UP) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        playerSnake.setDirection(Direction.UP);
                    } else {
                        playerSnake.enqueueDirection(Direction.UP);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_DOWN) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        playerSnake.setDirection(Direction.DOWN);
                    } else {
                        playerSnake.enqueueDirection(Direction.DOWN);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
            }
        });
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (running) {

            while (!scheduled.isEmpty()) {
                scheduled.poll().run();
            }

            if (!running) {
                return;
            }

            Direction oldDir = null;
            Coords oldCoords = null;
            if (playerSnake != null) {
                oldDir = playerSnake.getDirection();
                oldCoords = playerSnake.getCoords();
            }

            Iterator<Entity> it = entities.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                if (entity.isRemoved()) {
                    it.remove();

                    if (playerSnake != null && entity.getEntityId() == playerSnake.getEntityId()) {
                        playerSnake = null;
                    }
                    continue;
                }
                entity.update(delta);
            }

            if (playerSnake != null) {

                if (oldDir != playerSnake.getDirection()) {
                    client.unsafe().sendPacket(new UpdateDirection(playerSnake.getEntityId(), playerSnake.getDirection(), oldCoords));
                }

                Camera camera = renderer.getCamera();
                camera.coords.setX(playerSnake.getX() * camera.size - camera.width / 2.0).setY(playerSnake.getY() * camera.size - camera.height / 2.0);

                if (nameField.getText().equals("auto")) {

                    camera.coords.setX(10 * camera.size - camera.width / 2.0).setY(10 * camera.size - camera.height / 2.0);

                    /*if (playerSnake.getCoords().getY() > 20) {
                        playerSnake.setDirection(Direction.RIGHT);
                    }
                    if (playerSnake.getCoords().getX() > 20) {
                        playerSnake.setDirection(Direction.UP);
                    }
                    if (playerSnake.getCoords().getY() < 0) {
                        playerSnake.setDirection(Direction.LEFT);
                    }
                    if (playerSnake.getCoords().getX() < 0) {
                        playerSnake.setDirection(Direction.DOWN);
                    }*/
                }
            }

            renderer.update(delta);
        }
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        if (running) {
            renderer.render(g);
        }
        gui.render(g);
        if (showFps) {
            g.setColor(Color.red);
            g.drawString(container.getFPS() + "", 0, 0);
        }
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    public void schedule(Runnable r) {
        scheduled.offer(r);
    }

    public void createServer(int port) {
        if (connecting) {
            return;
        }
        connecting = true;
        if (client != null && connected) {
            disconnect();
        }
        if (localServer != null) {
            localServer.stop();
        }
        gui.setRoot(new InfoPanel(container, "Starting server..."));
        localServer = new SnakesServer(port);

        new Thread(() -> {
            try {
                localServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        connecting = false;
        connect("localhost", port);
    }

    public void connect(String ip, int port) {
        if (connecting) {
            return;
        }
        connecting = true;
        if (client != null && connected) {
            disconnect();
        }
        gui.setRoot(new InfoPanel(container, "Connecting..."));
        new Thread(() -> {
            try {
                client = new SnakesClient(Snakes.this, new InetSocketAddress(ip, port));
                client.start();
                client.connect(err -> {
                    if (err != null) {
                        gui.setRoot(new InfoPanel(container, err, btn -> gui.setRoot("connect")));
                    }
                });
            } finally {
                connecting = false;
            }
        }).start();
    }

    public void disconnect() {
        if (client != null) {
            client.stop();
        }
        if (localServer != null) {
            localServer.stop();
        }
    }

    @Override
    public void handle(UpdateDirection packet) throws Exception {
        schedule(() -> {
            for (Entity entity : entities) {
                if (entity.getEntityId() == packet.getEntityId()) {
                    SnakeEntity snake = (SnakeEntity) entity;
                    snake.updateDirection(packet.getDirection(), packet.getCoords());
                    return;
                }
            }
        });
    }

    @Override
    public void handle(SnakeTailSizeChange packet) throws Exception {
        schedule(() -> {
            for (Entity entity : entities) {
                if (entity.getEntityId() == packet.getEntityId()) {
                    SnakeEntity snake = (SnakeEntity) entity;
                    if (packet.getMod() > 0) {
                        snake.grow();
                    } else if (packet.getMod() < 0) {
                        snake.shrink();
                    }
                    return;
                }
            }
        });
    }

    @Override
    public void handle(SnakeTail packet) throws Exception {
        schedule(() -> {
            for (Entity entity : entities) {
                if (entity.getEntityId() == packet.getEntityId()) {
                    SnakeEntity snake = (SnakeEntity) entity;
                    snake.getTail().clear();
                    snake.getTail().addAll(packet.getTailAsList());
                    return;
                }
            }
        });
    }

    @Override
    public void handle(EntitySpawn packet) throws Exception {
        schedule(() -> {
            entities.add(packet.getEntity());
            if (packet.getEntityType() == EntityType.SNAKE) {
                SnakeEntity entity = (SnakeEntity) packet.getEntity();
                if (entity.getPlayerId() == playerId) {
                    playerSnake = entity;
                }
            }
        });
    }

    @Override
    public void handle(EntityRemove packet) throws Exception {
        schedule(() -> {
            for (Entity entity : entities) {
                if (entity.getEntityId() == packet.getEntityId()) {
                    entity.remove();
                    return;
                }
            }
        });
    }

    @Override
    public void handle(PlayerDeath packet) {
        //todo
    }

    @Override
    public void handle(PlayerLeave packet) throws Exception {
        players.remove(packet.getPlayerId());
    }

    @Override
    public void handle(PlayerJoin packet) throws Exception {
        players.put(packet.getPlayerId(), packet.getName());
    }

    @Override
    public void handle(MapData packet) throws Exception {
        mapBounds = packet.getMapBounds();
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception {
        playerId = loginSuccess.getPlayerId();
        System.out.println("logged in: player id = " + playerId);
        running = true; //start rendering the scene
        //todo gui.setRoot("overlay");
        gui.setNull();
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        if (connected) {
            throw new IllegalStateException("already connected");
        }
        connected = true;
        client.setChannel(channel);

        //todo show cancel button in connecting screen

        System.out.println("handshaking...");
        client.unsafe().sendPacket(new Handshake(true));
        System.out.println("logging in...");
        client.unsafe().sendPacket(new Login(nameField.getText(), VERSION));
    }

    @Override
    public void exception(Throwable t) throws Exception {
        System.out.println(this.toString() + ": Exception in ChannelAdapter: " + Utils.exception(t));
        disconnect();
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        if (!connected) {
            throw new IllegalStateException("not connected");
        }
        connected = false;
        running = false;
        System.out.println("Disconnected");
        disconnect();
        reset();
        gui.setRoot(new InfoPanel(container, "Disconnected", btn -> gui.setRoot("main")));
    }

    private void reset() {
        client = null;
        entities.clear();
        playerId = 0;
        mapBounds = null;
        scheduled.clear();
    }

    @Override
    public String getTitle() {
        return "Snakes";
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Gui getGui() {
        return gui;
    }

    public SnakesClient getClient() {
        return client;
    }

    public AxisAlignedBB getMapBounds() {
        return mapBounds;
    }

    public SnakeEntity getPlayerSnake() {
        return playerSnake;
    }
}

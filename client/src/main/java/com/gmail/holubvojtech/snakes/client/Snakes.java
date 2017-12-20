package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;
import com.gmail.holubvojtech.snakes.Utils;
import com.gmail.holubvojtech.snakes.client.gui.ConnectMenu;
import com.gmail.holubvojtech.snakes.client.gui.Gui;
import com.gmail.holubvojtech.snakes.client.gui.InfoPanel;
import com.gmail.holubvojtech.snakes.client.gui.MainMenu;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.entity.SnakeEntity;
import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.PacketHandler;
import org.newdawn.slick.*;
import org.newdawn.slick.util.InputAdapter;
import org.newdawn.slick.util.ResourceLoader;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Snakes extends PacketHandler implements Game {

    public static Snakes inst;
    public static Font font;

    private GameContainer container;
    private Gui gui;
    private boolean showFps = true;

    private boolean running;
    private boolean pauseUpdates;
    private GameRenderer renderer;

    private boolean connecting;
    private boolean connected;
    private SnakesClient client;

    private List<Entity> entities = new ArrayList<>();

    private long lastDirectionChange = 0;

    @Override
    public void init(GameContainer container) throws SlickException {
        inst = this;
        this.container = container;
        container.setShowFPS(false);
        container.setTargetFrameRate(60);

        try {
            font = new TrueTypeFont(
                    java.awt.Font.createFont(0, ResourceLoader.getResourceAsStream("kongtext.ttf")).deriveFont(16f),
                    false
            );
        } catch (Exception e) {
            throw new SlickException("cannot load font", e);
        }

        gui = new Gui(container.getInput());
        gui.savePanel("main", new MainMenu(container));
        gui.savePanel("connect", new ConnectMenu(container));
        gui.setRoot("main");

        renderer = new GameRenderer(new Camera(new Coords(-100, -100), container.getWidth(), container.getHeight()));
        SnakeEntity snakeEntity = new SnakeEntity(new Coords(0, 0));
        snakeEntity.getTail().add(Direction.UP);
        snakeEntity.getTail().add(Direction.UP);
        snakeEntity.getTail().add(Direction.UP);
        snakeEntity.getTail().add(Direction.LEFT);
        snakeEntity.getTail().add(Direction.LEFT);
        snakeEntity.getTail().add(Direction.LEFT);
        snakeEntity.getTail().add(Direction.LEFT);
        entities.add(snakeEntity);
        running = true;
        gui.setNull();

        container.getInput().addKeyListener(new InputAdapter() {
            @Override
            public void keyPressed(int key, char c) {
                if (key == Input.KEY_SPACE) {
                    pauseUpdates = !pauseUpdates;
                }
                if (key == Input.KEY_LEFT) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        ((SnakeEntity) entities.get(0)).setDirection(Direction.LEFT);
                    } else {
                        ((SnakeEntity) entities.get(0)).enqueueDirection(Direction.LEFT);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_RIGHT) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        ((SnakeEntity) entities.get(0)).setDirection(Direction.RIGHT);
                    } else {
                        ((SnakeEntity) entities.get(0)).enqueueDirection(Direction.RIGHT);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_UP) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        ((SnakeEntity) entities.get(0)).setDirection(Direction.UP);
                    } else {
                        ((SnakeEntity) entities.get(0)).enqueueDirection(Direction.UP);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_DOWN) {
                    if (System.currentTimeMillis() - lastDirectionChange > 150) {
                        ((SnakeEntity) entities.get(0)).setDirection(Direction.DOWN);
                    } else {
                        ((SnakeEntity) entities.get(0)).enqueueDirection(Direction.DOWN);
                    }
                    lastDirectionChange = System.currentTimeMillis();
                }
                if (key == Input.KEY_ENTER) {
                    SnakeEntity main = (SnakeEntity) entities.get(0);
                    Direction last = main.getTail().get(main.getTail().size() - 1);
                    main.getTail().add(last);
                }
            }
        });
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (pauseUpdates) {
            return;
        }
        if (running) {
            for (Entity entity : entities) {
                entity.update(delta);
            }
            SnakeEntity main = (SnakeEntity) entities.get(0);
            Camera camera = renderer.getCamera();
            camera.coords.setX(main.getX() * camera.size - camera.width / 2.0).setY(main.getY() * camera.size - camera.height / 2.0);
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

    public void connect(String ip, int port) {
        if (connecting) {
            return;
        }
        if (client != null && connected) {
            disconnect();
        }
        gui.setRoot(new InfoPanel(container, "Connecting..."));
        connecting = true;
        new Thread(() -> {
            try {
                client = new SnakesClient(Snakes.this, new InetSocketAddress(ip, port));
                client.start();
                client.connect(err -> {
                    if (err != null) {
                        gui.setRoot(new InfoPanel(container, err, btn -> gui.setRoot("connect")));
                        return;
                    }
                    running = true;
                });
            } finally {
                connecting = false;
            }
        }).start();
    }

    public void disconnect() {
        if (client != null) {
            client.stopSync();
        }
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        if (connected) {
            throw new IllegalStateException("already connected");
        }
        connected = true;
        client.setChannel(channel);
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
}

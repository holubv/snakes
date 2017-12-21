package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Utils;
import com.gmail.holubvojtech.snakes.client.gui.*;
import com.gmail.holubvojtech.snakes.entity.Entity;
import com.gmail.holubvojtech.snakes.netty.ChannelWrapper;
import com.gmail.holubvojtech.snakes.netty.PacketHandler;
import com.gmail.holubvojtech.snakes.protocol.packet.Handshake;
import com.gmail.holubvojtech.snakes.protocol.packet.Login;
import com.gmail.holubvojtech.snakes.protocol.packet.LoginSuccess;
import org.newdawn.slick.*;
import org.newdawn.slick.util.ResourceLoader;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Snakes extends PacketHandler implements Game {

    public static final String VERSION = "0.1 indev";

    public static Snakes inst;
    public static Font font;

    private GameContainer container;
    private Gui gui;
    private boolean showFps = true;

    private InputField nameField;

    private boolean running;
    private GameRenderer renderer;

    private boolean connecting;
    private boolean connected;
    private SnakesClient client;

    private int playerId;
    private List<Entity> entities = new ArrayList<>();

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
        MainMenu mm = new MainMenu(container);
        this.nameField = mm.getNameField();
        gui.savePanel("main", mm);
        gui.savePanel("connect", new ConnectMenu(container));
        gui.setRoot("main");

        renderer = new GameRenderer(new Camera(new Coords(), container.getWidth(), container.getHeight()));
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (running) {
            for (Entity entity : entities) {
                entity.update(delta);
            }
            //SnakeEntity main = (SnakeEntity) entities.get(0);
            //Camera camera = renderer.getCamera();
            //camera.coords.setX(main.getX() * camera.size - camera.width / 2.0).setY(main.getY() * camera.size - camera.height / 2.0);
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
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception {
        playerId = loginSuccess.getPlayerId();
        System.out.println("logged in: player id = " + playerId);
        gui.setNull();
        running = true; //start rendering the scene
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
        gui.setRoot(new InfoPanel(container, "Disconnected", btn -> gui.setRoot("main")));
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

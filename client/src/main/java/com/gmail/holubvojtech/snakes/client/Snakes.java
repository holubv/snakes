package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.client.gui.ConnectMenu;
import com.gmail.holubvojtech.snakes.client.gui.Gui;
import com.gmail.holubvojtech.snakes.client.gui.InfoMenu;
import com.gmail.holubvojtech.snakes.client.gui.MainMenu;
import org.newdawn.slick.*;
import org.newdawn.slick.util.ResourceLoader;

import java.net.InetSocketAddress;

public class Snakes implements Game {

    public static Snakes inst;
    public static Font font;

    private GameContainer container;
    private Gui gui;
    private boolean showFps = true;

    private boolean connecting;
    private SnakesClient client;

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
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
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
        if (client != null && client.isConnected()) {
            disconnect();
        }
        gui.setRoot(new InfoMenu(container, "Connecting..."));
        connecting = true;
        new Thread(() -> {
            client = new SnakesClient(new InetSocketAddress(ip, port));
            client.start();
            client.connect(err -> {
                if (err == null) {

                    return;
                }
                gui.setRoot(new InfoMenu(container, err, btn -> gui.setRoot("connect")));
            });
            connecting = false;
        }).start();
    }

    public void disconnect() {
        if (client != null) {
            client.stopSync();
        }
    }

    @Override
    public String getTitle() {
        return "Snakes";
    }

    public Gui getGui() {
        return gui;
    }
}

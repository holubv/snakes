package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.client.gui.ConnectMenu;
import com.gmail.holubvojtech.snakes.client.gui.Gui;
import com.gmail.holubvojtech.snakes.client.gui.MainMenu;
import org.newdawn.slick.*;
import org.newdawn.slick.util.ResourceLoader;

public class Snakes implements Game {

    public static Snakes inst;
    public static Font font;

    private Gui gui;

    public void init(GameContainer container) throws SlickException {
        inst = this;
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

    public void update(GameContainer container, int delta) throws SlickException {
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        gui.render(g);
    }

    public boolean closeRequested() {
        return true;
    }

    public String getTitle() {
        return "Snakes";
    }

    public Gui getGui() {
        return gui;
    }
}

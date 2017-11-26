package com.gmail.holubvojtech.snakes.client;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Snakes implements Game {

    public void init(GameContainer container) throws SlickException {
        container.setTargetFrameRate(60);
    }

    public void update(GameContainer container, int delta) throws SlickException {

    }

    public void render(GameContainer container, Graphics g) throws SlickException {

    }

    public boolean closeRequested() {
        return true;
    }

    public String getTitle() {
        return "Snakes";
    }
}

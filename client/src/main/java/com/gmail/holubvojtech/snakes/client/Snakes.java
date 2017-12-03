package com.gmail.holubvojtech.snakes.client;

import org.newdawn.slick.*;

public class Snakes implements Game {

    public static Font font;

    public void init(GameContainer container) throws SlickException {
        container.setTargetFrameRate(60);

        Image fontImg = new Image("kongtext_0.png");
        fontImg.setFilter(Image.FILTER_NEAREST);
        font = new AngelCodeFont("kongtext.fnt", fontImg);
    }

    public void update(GameContainer container, int delta) throws SlickException {

    }

    public void render(GameContainer container, Graphics g) throws SlickException {

        g.setFont(font);
        g.setColor(Color.white);
        g.setAntiAlias(false);
        g.drawString("Hello world!", 0, 0);
        g.resetTransform();
    }

    public boolean closeRequested() {
        return true;
    }

    public String getTitle() {
        return "Snakes";
    }
}

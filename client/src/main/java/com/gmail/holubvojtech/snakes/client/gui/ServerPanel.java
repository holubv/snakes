package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;

public class ServerPanel extends Panel {

    public ServerPanel(int x, int y, int width) {
        super(x, y, width, 58);

        setBackground(Color.lightGray);
        addChild(new Label(0, 0, "<empty slot>", Snakes.font));
        addChild(new Label(0, 20, "", Snakes.font));
        addChild(new Label(0, 40, "", Snakes.font));
    }
}

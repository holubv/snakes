package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;

public class ConnectMenu extends Panel {

    public ConnectMenu(GameContainer container) {
        super(0, 0, container.getWidth(), container.getHeight());

        setBackground(Color.white);

        addChild(new Label(10, 10, "Connect to the server", Snakes.font));

        addChild(
                new Button(10, height - 40,
                        new Label(0, 0, "Back", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> Snakes.inst.getGui().setRoot("main"))
        );

        addChild(new ServerPanel(10, 40, width - 20));
        addChild(new ServerPanel(10, 110, width - 20));
        addChild(new ServerPanel(10, 180, width - 20));
    }
}

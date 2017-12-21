package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;

public class MainMenu extends Panel {

    private InputField nameField;

    public MainMenu(GameContainer container) {
        super(0, 0, container.getWidth(), container.getHeight());

        setBackground(Color.white);

        addChild(new Label(10, 10, "Snakes v1", Snakes.font));
        this.nameField = new InputField(width - 266, 10, 240, Snakes.font).setHint("Nickname");
        addChild(this.nameField);

        addChild(
                new Button(width - 266, height - 80,
                        new Label(0, 0, "Create a server", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> {
                            //Snakes.inst.getGui().setRoot("main");
                        })
        );

        addChild(
                new Button(width - 362, height - 40,
                        new Label(0, 0, "Connect to the server", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> Snakes.inst.getGui().setRoot("connect"))
        );
    }

    public InputField getNameField() {
        return nameField;
    }
}

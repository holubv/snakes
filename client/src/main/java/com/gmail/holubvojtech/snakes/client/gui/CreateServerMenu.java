package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;

public class CreateServerMenu extends Panel {

    private InputField portField;

    public CreateServerMenu(GameContainer container) {
        super(0, 0, container.getWidth(), container.getHeight());

        setBackground(Color.white);

        addChild(new Label(10, 10, "Create a server", Snakes.font));

        addChild(
                new Button(10, height - 40,
                        new Label(0, 0, "Back", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> Snakes.inst.getGui().setRoot("main"))
        );

        portField = new InputField(10, 40, width - 36, Snakes.font).setHint("Port").setText("25565");

        addChild(portField);

        addChild(
                new Button(width - 122, height - 40,
                        new Label(0, 0, "Create", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> {
                            int port;
                            try {
                                port = Integer.parseInt(portField.getText());
                            } catch (NumberFormatException e) {
                                return;
                            }
                            if (port < 0 || port > 0xFFFF) {
                                return;
                            }
                            Snakes.inst.createServer(port);
                        })
        );
    }
}

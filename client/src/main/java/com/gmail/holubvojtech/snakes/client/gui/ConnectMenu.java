package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;

public class ConnectMenu extends Panel {

    private InputField serverIpField;
    private InputField portField;

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

        /*addChild(new ServerPanel(10, 40, width - 20));
        addChild(new ServerPanel(10, 110, width - 20));
        addChild(new ServerPanel(10, 180, width - 20));
        addChild(new InputField(10, 250, width - 36, Snakes.font).setHint("Server IP"));
        addChild(new InputField(10, 292, width - 36, Snakes.font).setHint("Port"));*/

        serverIpField = new InputField(10, 40, width - 36, Snakes.font).setHint("Server IP");
        portField = new InputField(10, 82, width - 36, Snakes.font).setHint("Port");

        addChild(serverIpField);
        addChild(portField);

        addChild(
                new Button(width - 138, height - 40,
                        new Label(0, 0, "Connect", Snakes.font)
                                .setColor(Color.white))
                        .setBackground(Color.black)
                        .setHoverBackground(Color.darkGray)
                        .onClick(btn -> {
                            String ip = serverIpField.getText();
                            int port;
                            try {
                                port = Integer.parseInt(portField.getText());
                            } catch (NumberFormatException e) {
                                return;
                            }
                            if (port < 0 || port > 0xFFFF) {
                                return;
                            }
                            Snakes.inst.connect(ip, port);
                        })
        );
    }
}

package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;

import java.util.function.Consumer;

public class InfoPanel extends Panel {

    public InfoPanel(GameContainer container, String text) {
        this(container, text, null);
    }

    public InfoPanel(GameContainer container, String text, Consumer<Button> okBtn) {
        this(container, new String[]{text}, okBtn);
    }

    public InfoPanel(GameContainer container, Throwable throwable, Consumer<Button> okBtn) {
        this(container, new String[]{throwable.getClass().getSimpleName() + ":", throwable.getMessage()}, okBtn);
    }

    public InfoPanel(GameContainer container, String[] text, Consumer<Button> okBtn) {
        super(0, 0, container.getWidth(), container.getHeight());
        setBackground(Color.white);

        int th = 0;

        for (String line : text) {
            th += Snakes.font.getHeight(line) + 8;
        }

        int oh = 0;
        for (String line : text) {
            int w = Snakes.font.getWidth(line);
            addChild(new Label(width / 2 - w / 2, height / 2 - th / 2 + oh, line, Snakes.font));
            oh += Snakes.font.getHeight(line) + 8;
        }

        if (okBtn != null) {
            addChild(
                    new Button(width - 58, height - 40,
                            new Label(0, 0, "Ok", Snakes.font)
                                    .setColor(Color.white))
                            .setBackground(Color.black)
                            .setHoverBackground(Color.darkGray)
                            .onClick(okBtn)
            );
        }
    }
}

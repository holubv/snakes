package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.util.function.Consumer;

public class Button extends Component {

    private Label label;
    private int padX;
    private int padY;

    private Color background;
    private Color hoverBackground;

    private Consumer<Button> onClick;

    public Button(int x, int y, Label label) {
        this(x, y, label, 8, 6);
    }

    public Button(int x, int y, Label label, int paddingX, int paddingY) {
        super(x, y, label.getWidth() + paddingX * 2, label.getHeight() + paddingY * 2);
        this.label = label;
        this.padX = paddingX;
        this.padY = paddingY;
    }

    public Button(int x, int y, String text, Font font) {
        this(x, y, text, font, 8, 6);
    }

    public Button(int x, int y, String text, Font font, int paddingX, int paddingY) {
        this(x, y, new Label(0, 0, text, font), paddingX, paddingY);
    }

    public Label getLabel() {
        return label;
    }

    public Color getBackground() {
        return background;
    }

    public Button setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Color getHoverBackground() {
        return hoverBackground;
    }

    public Button setHoverBackground(Color hoverBackground) {
        this.hoverBackground = hoverBackground;
        return this;
    }

    public Button onClick(Consumer<Button> click) {
        this.onClick = click;
        return this;
    }

    public void notifyLabelUpdate() {
        this.width = label.getWidth() + padX * 2;
        this.height = label.getHeight() + padY * 2;
    }

    @Override
    public void render(Graphics g) {

        if (mouseOver) {
            if (hoverBackground != null) {
                g.setColor(hoverBackground);
                g.fillRect(x, y, width, height);
            }
        } else {
            if (background != null) {
                g.setColor(background);
                g.fillRect(x, y, width, height);
            }
        }

        g.pushTransform();
        g.translate(x + padX, y + padY);
        label.render(g);
        g.popTransform();
    }

    @Override
    protected void onMouseClick(int button, int x, int y) {
        if (onClick != null) {
            onClick.accept(this);
        }
    }
}

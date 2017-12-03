package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class Label extends Component {

    private Font font;
    private String text;
    private Color color = Color.black;

    public Label(int x, int y, String text, Font font) {
        super(x, y, font.getWidth(text), font.getHeight(text));
        this.text = text;
        this.font = font;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public Font getFont() {
        return font;
    }

    public Label setFont(Font font) {
        this.font = font;
        computeSize();
        return this;
    }

    public String getText() {
        return text;
    }

    public Label setText(String text) {
        this.text = text;
        computeSize();
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Label setColor(Color color) {
        this.color = color;
        return this;
    }

    private void computeSize() {
        this.width = this.font.getWidth(this.text);
        this.height = this.font.getHeight(this.text);
    }
}

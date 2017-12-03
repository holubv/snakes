package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class Panel extends Component {

    private List<Component> children = new ArrayList<>();

    private Color background;

    public Panel(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Panel addChild(Component component) {
        this.children.add(component);
        System.out.println("adding child: " + component.getClass().getSimpleName() + " w:" + component.getWidth() + ", h:" + component.getHeight());
        return this;
    }

    public Panel removeChild(Component component) {
        this.children.remove(component);
        return this;
    }

    public Color getBackground() {
        return background;
    }

    public Panel setBackground(Color background) {
        this.background = background;
        return this;
    }

    @Override
    public void render(Graphics g) {
        g.pushTransform();
        g.translate(x, y);
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }
        for (Component component : children) {
            component.render(g);
        }
        g.popTransform();
    }

    @Override
    protected void onKeyPress(int key, char c) {
        for (Component component : children) {
            component.onKeyPress(key, c);
        }
    }

    @Override
    protected void onKeyRelease(int key, char c) {
        for (Component component : children) {
            component.onKeyRelease(key, c);
        }
    }

    @Override
    protected void onMouseClick(int button, int x, int y) {
        x -= this.x;
        y -= this.y;

        for (Component component : children) {
            if (component.contains(x, y)) {
                if (!component.hasFocus) {
                    component.hasFocus = true;
                    component.onFocusChange();
                }
                component.onMouseClick(button, x, y);
            } else {
                if (component.hasFocus) {
                    component.hasFocus = false;
                    component.onFocusChange();
                }
                if (component instanceof Panel) {
                    component.onMouseClick(button, x, y);
                }
            }
        }
    }

    @Override
    protected void onMouseOver(int x, int y) {
        x -= this.x;
        y -= this.y;

        for (Component component : children) {
            if (component.contains(x, y)) {
                component.mouseOver = true;
                component.onMouseOver(x, y);
            } else {
                if (component.mouseOver) {
                    component.mouseOver = false;
                    component.onMouseLeave();
                }
            }
        }
    }
}

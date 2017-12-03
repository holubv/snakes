package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Graphics;

public abstract class Component {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected boolean hasFocus;
    protected boolean mouseOver;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(Graphics g);

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x < (this.x + width) &&
                y >= this.y && y < (this.y + height);
    }

    protected void onKeyPress(int key, char c) {
    }

    protected void onKeyRelease(int key, char c) {
    }

    protected void onMouseClick(int button, int x, int y) {
    }

    protected void onMousePress(int button, int x, int y) {
    }

    protected void onMouseRelease(int button, int x, int y) {
    }

    protected void onMouseOver(int x, int y) {
    }

    protected void onWheelMove(int change) {
    }

    protected void onMouseLeave() {
    }

    protected void onFocusChange() {
    }
}

package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;

import java.util.HashMap;
import java.util.Map;

public class Gui implements MouseListener, KeyListener {

    private static final NullRoot NULL_ROOT = new NullRoot();

    private Panel root = NULL_ROOT;
    private Input input;

    private Map<String, Panel> savedPanels = new HashMap<>();

    public Gui(Input input) {
        this(input, null);
    }

    public Gui(Input input, Panel root) {
        this.input = input;
        this.root = root;

        input.addMouseListener(this);
        input.addKeyListener(this);
    }

    public void render(Graphics g) {
        if (root == null) {
            return;
        }
        g.pushTransform();
        root.render(g);
        g.popTransform();
    }

    public Panel getRoot() {
        return root;
    }

    public Gui setNull() {
        this.root = NULL_ROOT;
        return this;
    }

    public Gui setRoot(Panel root) {
        if (root == null) {
            root = NULL_ROOT;
        }
        this.root = root;
        return this;
    }

    public Gui setRoot(String key) {
        Panel panel = savedPanels.get(key);
        if (panel != null) {
            this.root = panel;
        }
        return this;
    }

    public Gui savePanel(String key, Panel panel) {
        savedPanels.put(key, panel);
        return this;
    }

    @Override
    public void keyPressed(int key, char c) {
        root.onKeyPress(key, c);
    }

    @Override
    public void keyReleased(int key, char c) {
        root.onKeyRelease(key, c);
    }

    @Override
    public void mouseWheelMoved(int change) {
        root.onWheelMove(change);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        root.onMouseClick(button, x, y);
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        root.onMousePress(button, x, y);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        root.onMouseRelease(button, x, y);
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        root.onMouseOver(newx, newy);
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {
    }

    private static class NullRoot extends Panel {
        public NullRoot() {
            super(0, 0, 0, 0);
        }

        @Override
        public void render(Graphics g) {
        }

        @Override
        protected void onKeyPress(int key, char c) {
        }

        @Override
        protected void onKeyRelease(int key, char c) {
        }

        @Override
        protected void onMouseClick(int button, int x, int y) {
        }

        @Override
        protected void onMouseOver(int x, int y) {
        }
    }
}

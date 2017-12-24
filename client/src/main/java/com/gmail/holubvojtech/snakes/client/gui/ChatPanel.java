package com.gmail.holubvojtech.snakes.client.gui;

import com.gmail.holubvojtech.snakes.client.Snakes;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayDeque;
import java.util.Deque;

public class ChatPanel extends Panel {

    private boolean visible;
    private ChatComponent chat;
    private InputField chatField;

    private Deque<String> messages = new ArrayDeque<>();

    public ChatPanel(int x, int y, int width, int height) {
        super(x, y, width, height);

        setBackground(new Color(211, 211, 211, 90));

        chat = new ChatComponent(0, 0, width, height - (Snakes.font.getLineHeight() + 6 * 2));
        chatField = new InputField(0, chat.getHeight(), width - 8 * 2, Snakes.font);

        addChild(chat);
        addChild(chatField);
    }

    public void showMessage(String message) {
        messages.addFirst(message);
        if (messages.size() > 15) {
            messages.removeLast();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void render(Graphics g) {
        if (visible) {
            super.render(g);
        }
    }

    private class ChatComponent extends Component {

        public ChatComponent(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void render(Graphics g) {
            int y = getHeight();
            int line = Snakes.font.getLineHeight();
            for (String msg : messages) {
                y -= line;
                if (y < getY()) {
                    return;
                }
                g.setColor(Color.black);
                g.drawString(msg, getX(), y);
            }
        }
    }
}

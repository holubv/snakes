package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.GameContainer;

public class GameOverlay extends Panel {

    private ChatPanel chatPanel;

    public GameOverlay(GameContainer container) {
        super(0, 0, container.getWidth(), container.getHeight());
        setBackground(null);

        chatPanel = new ChatPanel(0, getHeight() / 2, getWidth() / 2, getHeight() / 2);
        addChild(chatPanel);

        chatPanel.showMessage("Test message");
        chatPanel.showMessage("Test message2");
        chatPanel.showMessage("Test message3");
        chatPanel.showMessage("Test message4");
        chatPanel.showMessage("Test message5");
        chatPanel.showMessage("Test message6");
        chatPanel.showMessage("Test message7");
        chatPanel.showMessage("Test message8");
        chatPanel.showMessage("Test message9");
        chatPanel.showMessage("Test message10");
        chatPanel.showMessage("Test message11");
        chatPanel.showMessage("Test message12");
    }

    public void showMessage(String message) {
        chatPanel.showMessage(message);
    }

    public void setChatVisible(boolean visible) {
        this.chatPanel.setVisible(visible);
    }

    public boolean isChatVisible() {
        return this.chatPanel.isVisible();
    }
}

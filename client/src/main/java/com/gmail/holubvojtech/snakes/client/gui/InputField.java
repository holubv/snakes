package com.gmail.holubvojtech.snakes.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * Simple version of text input, does not support text rolling
 * todo text moving/rolling/overflow
 * todo caret blinking
 * todo change caret position using mouse click
 */
public class InputField extends Component {

    private Font font;
    private String text = "";
    private StringBuilder sb = new StringBuilder();
    private int cursor = 0;
    private int strLen = 0;
    private int cursorPx = 0;

    private String hint;

    private int padX;
    private int padY;

    private Color background;
    private Color frameColor = Color.black;
    private Color caretColor = Color.black;
    private Color color = Color.black;
    private Color hintColor = Color.lightGray;

    public InputField(int x, int y, int width, Font font) {
        this(x, y, width, font, 8, 6);
    }

    public InputField(int x, int y, int width, Font font, int padX, int padY) {
        super(x, y, width + padX * 2, font.getLineHeight() + padY * 2);
        this.font = font;
        this.padX = padX;
        this.padY = padY;
    }

    @Override
    public void render(Graphics g) {

        Font oldFont = g.getFont();
        g.setFont(font);

        if (background != null) {
            g.setColor(background);
            g.fillRect(x, y, width, height);
        }

        if (frameColor != null) {
            g.setColor(frameColor);
            g.drawRect(x, y, width, height);
        }

        g.setColor(color);
        g.drawString(text, padX + x, padY + y);

        if (hasFocus) {
            g.setColor(caretColor);
            g.fillRect(padX + x + cursorPx, padY + y, 3, height - padY * 2);
        } else {
            if (hint != null && text.isEmpty()) {
                g.setColor(hintColor);
                g.drawString(hint, padX + x, padY + y);
            }
        }

        g.setFont(oldFont);
    }

    @Override
    protected void onKeyPress(int key, char c) {
        if (!hasFocus) {
            return;
        }
        if (key == Input.KEY_LEFT && cursor > 0) {
            cursor--;
            updateCursor();
            return;
        }
        if (key == Input.KEY_RIGHT && cursor < strLen) {
            cursor++;
            updateCursor();
            return;
        }
        if (key == Input.KEY_HOME) {
            cursor = 0;
            updateCursor();
            return;
        }
        if (key == Input.KEY_END) {
            cursor = strLen;
            updateCursor();
            return;
        }
        if (key == Input.KEY_DELETE && cursor < strLen) {
            sb.deleteCharAt(cursor);
            text = sb.toString();
            strLen = sb.length();
            return;
        }
        if (key == Input.KEY_BACK && cursor > 0) {
            cursor--;
            sb.deleteCharAt(cursor);
            text = sb.toString();
            strLen = sb.length();
            updateCursor();
            return;
        }
        c = replaceDiacritic(c);
        if (isValidChar(c)) {
            sb.insert(cursor, c);
            cursor++;
            text = sb.toString();
            strLen = sb.length();
            updateCursor();
        }
    }

    public String getText() {
        return text;
    }

    public InputField setText(String text) {

        cursor = 0;
        sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            c = replaceDiacritic(c);
            if (isValidChar(c)) {
                sb.insert(cursor, c);
                cursor++;
            }
        }

        this.text = sb.toString();
        strLen = sb.length();
        cursor = strLen;
        updateCursor();
        return this;
    }

    public String getHint() {
        return hint;
    }

    public InputField setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public Color getBackground() {
        return background;
    }

    public InputField setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Color getFrameColor() {
        return frameColor;
    }

    public InputField setFrameColor(Color frameColor) {
        this.frameColor = frameColor;
        return this;
    }

    public Color getCaretColor() {
        return caretColor;
    }

    public InputField setCaretColor(Color caretColor) {
        this.caretColor = caretColor;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public InputField setColor(Color color) {
        this.color = color;
        return this;
    }

    public Color getHintColor() {
        return hintColor;
    }

    public InputField setHintColor(Color hintColor) {
        this.hintColor = hintColor;
        return this;
    }

    private void updateCursor() {
        cursorPx = font.getWidth(sb.substring(0, cursor));
    }

    private boolean isValidChar(char c) {
        return c >= 32 && c <= 126;
    }

    private char replaceDiacritic(char c) {
        boolean up = Character.isUpperCase(c);
        if (up) {
            c = Character.toLowerCase(c);
        }
        c = replaceDiacriticLower(c);
        if (up) {
            c = Character.toUpperCase(c);
        }
        return c;
    }

    private char replaceDiacriticLower(char c) {
        switch (c) {
            case 'ě':
                return 'e';
            case 'š':
                return 's';
            case 'č':
                return 'c';
            case 'ř':
                return 'r';
            case 'ž':
                return 'z';
            case 'ý':
                return 'y';
            case 'á':
                return 'a';
            case 'í':
                return 'i';
            case 'é':
                return 'e';
            case 'ú':
                return 'u';
            case 'ů':
                return 'u';
            case 'ľ':
                return 'l';
            case 'ä':
                return 'a';
            case 'ó':
                return 'o';
            case 'ť':
                return 't';
        }
        return c;
    }
}

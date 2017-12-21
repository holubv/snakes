package com.gmail.holubvojtech.snakes;

public class Color {

    private byte r;
    private byte g;
    private byte b;

    public Color(int r, int g, int b) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
    }

    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }
}

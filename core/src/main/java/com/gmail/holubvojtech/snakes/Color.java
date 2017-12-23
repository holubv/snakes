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

    public Color(int rgb) {
        r = (byte) ((rgb >> 16) & 0xFF);
        g = (byte) ((rgb >> 8) & 0xFF);
        b = (byte) (rgb & 0xFF);
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

    public int getRGB() {
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }
}

package com.gmail.holubvojtech.snakes.client;

import com.gmail.holubvojtech.snakes.Coords;

public class Camera {

    public int size = 16;

    public Coords coords;

    public int width;
    public int height;

    public Camera(Coords coords, int width, int height) {
        this.coords = coords;
        this.width = width;
        this.height = height;
    }

    public boolean isInViewport(Coords coords) {
        return isInViewport(coords.getX(), coords.getY());
    }

    public boolean isInViewport(double x, double y) {
        return true; //TODO: viewport check
    }

    public Coords transform(Coords coords) {
        return new Coords(coords.getX() * size - this.coords.getX(), coords.getY() * size - this.coords.getY());
    }

    public double transformX(double x) {
        return x * size - this.coords.getX();
    }

    public double transformY(double y) {
        return y * size - this.coords.getY();
    }

    public Camera copy() {
        Camera c = new Camera(coords.copy(), width, height);
        c.size = size;

        return c;
    }
}

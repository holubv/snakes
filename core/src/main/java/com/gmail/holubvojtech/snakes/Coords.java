package com.gmail.holubvojtech.snakes;

public class Coords {

    private double x;
    private double y;

    public Coords() {
    }

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coords(Coords coords) {
        this(coords.x, coords.y);
    }

    public double getX() {
        return x;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public Coords setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public Coords setY(double y) {
        this.y = y;
        return this;
    }

    public Coords add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public double distance(Coords coords) {
        return Math.sqrt(distanceSquared(coords));
    }

    public double distanceSquared(Coords coords) {
        double dx = x - coords.x;
        double dy = y - coords.y;
        return (dx * dx) + (dy * dy);
    }

    public Coords copy() {
        return new Coords(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords coords = (Coords) o;
        return Double.compare(coords.x, x) == 0 && Double.compare(coords.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

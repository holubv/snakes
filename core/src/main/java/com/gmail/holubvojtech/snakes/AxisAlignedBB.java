package com.gmail.holubvojtech.snakes;

public class AxisAlignedBB {

    private Coords coords;
    private double width;
    private double height;

    public AxisAlignedBB() {
        this.coords = new Coords();
    }

    public AxisAlignedBB(Coords coords, double width, double height) {
        this.coords = coords.copy();
        this.width = width;
        this.height = height;
    }

    public AxisAlignedBB(double x, double y, double width, double height) {
        this.coords = new Coords(x, y);
        this.width = width;
        this.height = height;
    }

    public AxisAlignedBB grow(Direction direction, double amount) {
        if (amount > 0) {
            if (direction.isNegative()) {
                add(direction.getRx() * amount, direction.getRy() * amount);
            }
            grow(Math.abs(direction.getRx() * amount), Math.abs(direction.getRy() * amount));
        } else {
            if (!direction.isNegative()) {
                add(direction.getRx() * Math.abs(amount), direction.getRy() * Math.abs(amount));
            }
            grow(-Math.abs(direction.getRx() * amount), -Math.abs(direction.getRy() * amount));
        }
        return this;
    }

    public AxisAlignedBB grow(double dx, double dy) {
        width += dx;
        height += dy;
        return this;
    }

    public AxisAlignedBB move(Direction direction, double amount) {
        add(direction.getRx() * amount, direction.getRy() * amount);
        return this;
    }

    public AxisAlignedBB include(Coords coords) {

        if (coords.getX() < getX()) {
            grow(Direction.LEFT, Math.abs(coords.getX() - getX()));
        } else if (coords.getX() > getX() + width) {
            grow(Direction.RIGHT, Math.abs(coords.getX() - (getX() + width)));
        }

        if (coords.getY() < getY()) {
            grow(Direction.UP, Math.abs(coords.getY() - getY()));
        } else if (coords.getY() > getY() + height) {
            grow(Direction.DOWN, Math.abs(coords.getY() - (getY() + height)));
        }

        return this;
    }

    public boolean contains(Coords coords) {
        return getX() <= coords.getX() && coords.getX() < (getX() + width) &&
                getY() <= coords.getY() && coords.getY() < (getY() + height);
    }

    public boolean contains(AxisAlignedBB aabb) {
        return (aabb.getX() >= getX() &&
                aabb.getY() >= getY() &&
                (aabb.getX() + aabb.getWidth()) <= getX() + width &&
                (aabb.getY() + aabb.getHeight()) <= getY() + height);
    }

    public boolean intersects(AxisAlignedBB aabb) {
        return (aabb.getX() + aabb.getWidth() > getX() &&
                aabb.getY() + aabb.getHeight() > getY() &&
                aabb.getX() < getX() + getWidth() &&
                aabb.getY() < getY() + getHeight());
    }

    public double getX() {
        return coords.getX();
    }

    public AxisAlignedBB setX(double x) {
        coords.setX(x);
        return this;
    }

    public double getY() {
        return coords.getY();
    }

    public AxisAlignedBB setY(double y) {
        coords.setY(y);
        return this;
    }

    public AxisAlignedBB add(double x, double y) {
        coords.add(x, y);
        return this;
    }

    public Coords getCoords() {
        return coords;
    }

    public AxisAlignedBB setCoords(Coords coords) {
        this.coords = coords.copy();
        return this;
    }

    public double getWidth() {
        return width;
    }

    public AxisAlignedBB setWidth(double width) {
        this.width = width;
        return this;
    }

    public double getHeight() {
        return height;
    }

    public AxisAlignedBB setHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "AxisAlignedBB{" +
                "coords=" + coords +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

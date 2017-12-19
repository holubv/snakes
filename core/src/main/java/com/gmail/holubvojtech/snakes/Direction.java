package com.gmail.holubvojtech.snakes;

public enum Direction {

    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    int rx;
    int ry;

    Direction(int rx, int ry) {
        this.rx = rx;
        this.ry = ry;
    }

    public Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        throw new IllegalStateException();
    }

    public Coords set(Coords coords) {
        return coords.add(rx, ry);
    }

    public int getRx() {
        return rx;
    }

    public int getRy() {
        return ry;
    }

    public boolean isNegative() {
        return rx < 0 || ry < 0;
    }

    public static Direction getDirection(Coords start, Coords end) {
        double dx = Math.abs(end.getX() - start.getX());
        double dy = Math.abs(end.getY() - start.getY());

        if (dx > dy) {
            if (end.getX() > start.getX()) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        } else {
            if (end.getY() > start.getY()) {
                return Direction.DOWN;
            } else {
                return Direction.UP;
            }
        }
    }
}

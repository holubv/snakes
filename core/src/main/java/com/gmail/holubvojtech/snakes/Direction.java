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
}

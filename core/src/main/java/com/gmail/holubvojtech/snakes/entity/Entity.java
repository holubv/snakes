package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;

public abstract class Entity {

    public static int nextEntityID = 1;

    protected int entityId;
    protected Coords coords;

    public Entity(Coords coords) {
        this(nextEntityID++, coords);
    }

    public Entity(int entityId, Coords coords) {
        this.entityId = entityId;
        this.coords = coords;
    }

    public void teleport(Coords coords) {
        teleport(coords.getX(), coords.getY());
    }

    public void teleport(double x, double y) {
        this.coords.setX(x).setY(y);
    }

    public void update(int delta) {
    }

    public abstract void render(AbstractRenderer renderer, Object context);

    public int getEntityId() {
        return entityId;
    }

    public double getX() {
        return coords.getX();
    }

    public double getY() {
        return coords.getY();
    }

    public Coords getCoords() {
        return coords.copy();
    }
}

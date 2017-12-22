package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity {

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(1);

    protected final int entityId;
    protected final EntityType type;
    protected Coords coords;

    protected boolean removed;

    public Entity(EntityType type, Coords coords) {
        this(NEXT_ENTITY_ID.getAndIncrement(), type, coords);
    }

    public Entity(int entityId, EntityType type, Coords coords) {
        this.entityId = entityId;
        this.type = type;
        this.coords = coords.copy();
    }

    public void teleport(Coords coords) {
        teleport(coords.getX(), coords.getY());
    }

    public void teleport(double x, double y) {
        this.coords.setX(x).setY(y);
    }

    public void update(double delta) {
    }

    public abstract void render(AbstractRenderer renderer, Object context);

    public int getEntityId() {
        return entityId;
    }

    public EntityType getType() {
        return type;
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

    public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityId=" + entityId +
                ", type=" + type +
                ", coords=" + coords +
                ", removed=" + removed +
                '}';
    }
}

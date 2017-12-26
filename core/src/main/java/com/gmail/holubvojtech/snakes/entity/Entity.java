package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.AxisAlignedBB;
import com.gmail.holubvojtech.snakes.Coords;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity {

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(1);

    protected final int entityId;
    protected final EntityType type;
    protected Coords coords;

    protected AxisAlignedBB boundingBox;

    protected boolean removed;

    public Entity(EntityType type, Coords coords) {
        this(NEXT_ENTITY_ID.getAndIncrement(), type, coords);
    }

    public Entity(int entityId, EntityType type, Coords coords) {
        this.entityId = entityId;
        this.type = type;
        this.coords = coords.copy();
    }

    public static boolean collides(Entity e1, Entity e2) {
        AxisAlignedBB aabb1 = e1.getBoundingBox();
        AxisAlignedBB aabb2 = e2.getBoundingBox();
        if (aabb1 == null || aabb2 == null) {
            return false;
        }
        if (!aabb1.intersects(aabb2)) {
            return false;
        }
        if (e1 instanceof CompoundAABBEntity && e2 instanceof CompoundAABBEntity) {
            for (AxisAlignedBB box1 : ((CompoundAABBEntity) e1).getBoundingBoxes()) {
                for (AxisAlignedBB box2 : ((CompoundAABBEntity) e2).getBoundingBoxes()) {
                    if (box1.intersects(box2)) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (e1 instanceof CompoundAABBEntity) {
            return CompoundAABBEntity.intersects((CompoundAABBEntity) e1, aabb2);
        }
        //noinspection SimplifiableIfStatement
        if (e2 instanceof CompoundAABBEntity) {
            return CompoundAABBEntity.intersects((CompoundAABBEntity) e2, aabb1);
        }
        return true;
    }

    public static AxisAlignedBB intersects(AxisAlignedBB aabb, CompoundAABBEntity entity, boolean ignoreFirst) {
        int i = -1;
        for (AxisAlignedBB box : entity.getBoundingBoxes()) {
            i++;
            if (i == 0 && ignoreFirst) {
                continue;
            }
            if (aabb.intersects(box)) {
                return box;
            }
        }
        return null;
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

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
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

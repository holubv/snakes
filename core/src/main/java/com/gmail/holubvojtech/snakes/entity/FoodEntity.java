package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.AxisAlignedBB;
import com.gmail.holubvojtech.snakes.Coords;

public class FoodEntity extends Entity {

    private Type type = Type.GROW;

    public FoodEntity(Coords coords) {
        super(EntityType.FOOD, coords);
        boundingBox = new AxisAlignedBB(coords, 1, 1);
    }

    public FoodEntity(int entityId, Coords coords) {
        super(entityId, EntityType.FOOD, coords);
        boundingBox = new AxisAlignedBB(coords, 1, 1);
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    public Type getFoodType() {
        return type;
    }

    public FoodEntity setType(Type type) {
        this.type = type;
        return this;
    }

    public enum Type {
        GROW,
        SHRINK
    }
}

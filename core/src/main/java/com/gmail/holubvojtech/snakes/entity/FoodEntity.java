package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;

public class FoodEntity extends Entity {

    private Type type;

    public FoodEntity(Coords coords, Type foodType) {
        super(EntityType.FOOD, coords);
        this.type = foodType;
    }

    public FoodEntity(int entityId, Coords coords, Type foodType) {
        super(entityId, EntityType.FOOD, coords);
        this.type = foodType;
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    public Type getFoodType() {
        return type;
    }

    public enum Type {
        GROW,
        SHRINK
    }
}

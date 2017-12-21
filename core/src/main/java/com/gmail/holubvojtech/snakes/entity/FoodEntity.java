package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;

public class FoodEntity extends Entity {

    public FoodEntity(Coords coords) {
        super(EntityType.FOOD, coords);
    }

    public FoodEntity(int entityId, Coords coords) {
        super(entityId, EntityType.FOOD, coords);
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    public enum Effect {
        GROW,
        SHRINK
    }
}

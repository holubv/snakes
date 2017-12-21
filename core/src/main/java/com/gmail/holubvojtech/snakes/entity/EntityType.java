package com.gmail.holubvojtech.snakes.entity;

public enum EntityType {

    SNAKE(SnakeEntity.class),
    FOOD(FoodEntity.class);

    private Class<? extends Entity> clazz;

    EntityType(Class<? extends Entity> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Entity> getClazz() {
        return clazz;
    }
}

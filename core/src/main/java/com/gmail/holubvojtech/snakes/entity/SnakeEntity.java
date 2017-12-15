package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;

import java.util.Vector;

public class SnakeEntity extends Entity {

    private Direction direction = Direction.DOWN;
    private Vector<Direction> tail = new Vector<>(32);

    private double speed = 0.005;

    public SnakeEntity(Coords coords) {
        super(coords);
    }

    public SnakeEntity(int entityId, Coords coords) {
        super(entityId, coords);
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        coords.add(direction.getRx() * speed * delta, direction.getRy() * speed * delta);
    }

    @Override
    public void teleport(double x, double y) {
        super.teleport(x, y);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Vector<Direction> getTail() {
        return tail;
    }

    public int getLength() {
        return tail.size();
    }
}

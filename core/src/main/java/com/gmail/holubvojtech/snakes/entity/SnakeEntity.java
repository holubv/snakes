package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AbstractRenderer;
import com.gmail.holubvojtech.snakes.Coords;
import com.gmail.holubvojtech.snakes.Direction;

import java.util.LinkedList;
import java.util.List;

public class SnakeEntity extends Entity {

    private Direction direction = Direction.DOWN;
    private LinkedList<Direction> tail = new LinkedList<>();
    private Coords tailPivot;
    private Coords lastPivot;

    private double speed = 0.0055;
    private int lx;
    private int ly;

    private LinkedList<Direction> nextDirection = new LinkedList<>();
    private Direction lastDirection = direction;


    public SnakeEntity(Coords coords) {
        super(coords);
        tailPivot = coords.blockCoords();
    }

    public SnakeEntity(int entityId, Coords coords) {
        super(entityId, coords);
        tailPivot = coords.blockCoords();
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        double distance = speed * delta;
        coords.add(direction.getRx() * distance, direction.getRy() * distance);

        boolean changed = false;

        switch (lastDirection) {
            case UP:
                changed = ly - 0.9 > coords.getY();
                break;
            case LEFT:
                changed = lx - 0.9 > coords.getX();
                break;
            case DOWN:
                changed = ly < coords.getBlockY();
                break;
            case RIGHT:
                changed = lx < coords.getBlockX();
                break;
        }

        if (changed) {

            System.out.println("changed " + System.currentTimeMillis());

            if (!nextDirection.isEmpty() && nextDirection.peek() != direction) {
                lastDirection = direction;
                direction = nextDirection.pop();
                coords.setX(Math.round(coords.getX()));
                coords.setY(Math.round(coords.getY()));
            }

            lx = coords.getBlockX();
            ly = coords.getBlockY();
        }

        lastPivot = tailPivot.copy();

        if (direction.isNegative() && lastDirection == direction) {
            tailPivot.setX(coords.getBlockX() - direction.getRx()).setY(coords.getBlockY() - direction.getRy());
        } else {
            tailPivot.setX(coords.getBlockX()).setY(coords.getBlockY());
        }

        if (tailPivot.equals(lastPivot)) {
            return;
        }

        tail.removeLast();
        tail.addFirst(lastDirection.opposite());

        lastDirection = direction;
    }

    @Override
    public void teleport(double x, double y) {
        super.teleport(x, y);
    }

    public void setDirection(Direction direction) {
        if (direction == this.direction || direction == this.direction.opposite()) {
            return;
        }
        this.nextDirection.clear();
        this.nextDirection.add(direction);
    }

    public void enqueueDirection(Direction direction) {
        Direction last = nextDirection.peekLast();
        if (last == null) {
            setDirection(direction);
            return;
        }
        if (direction == last || direction == last.opposite()) {
            return;
        }
        nextDirection.add(direction);
    }

    public LinkedList<Direction> getNextDirection() {
        return nextDirection;
    }

    public Direction getDirection() {
        return direction;
    }

    public Coords getTailPivot() {
        return tailPivot.copy();
    }

    public List<Direction> getTail() {
        return tail;
    }
}

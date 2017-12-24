package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SnakeEntity extends Entity implements CompoundAABBEntity {

    private Direction direction = Direction.DOWN;
    private LinkedList<Direction> tail = new LinkedList<>();
    private Coords tailPivot;
    private Coords lastPivot;

    private LinkedList<AxisAlignedBB> collisionBoxes = new LinkedList<>();

    private int playerId;

    private double speed = 0.0015;
    private int lx;
    private int ly;

    private Color color = new Color(0, 0, 0);

    private LinkedList<Direction> nextDirection = new LinkedList<>();
    private Direction lastDirection = direction;

    public SnakeEntity(Coords coords) {
        super(EntityType.SNAKE, coords);
        tailPivot = coords.blockCoords();
        boundingBox = new AxisAlignedBB();
    }

    public SnakeEntity(int entityId, Coords coords) {
        super(entityId, EntityType.SNAKE, coords);
        tailPivot = coords.blockCoords();
        boundingBox = new AxisAlignedBB();
    }

    @Override
    public void render(AbstractRenderer renderer, Object context) {
        renderer.render(this, context);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        update0(delta, false);
    }

    private void update0(double delta, boolean forceChange) {
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
                changed = ly + 0.9 < coords.getY();
                break;
            case RIGHT:
                changed = lx + 0.9 < coords.getX();
                break;
        }

        if (changed || forceChange) {

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

        if (!tail.isEmpty()) {
            tail.removeLast();
            tail.addFirst(lastDirection.opposite());
        }


        boundingBox.setCoords(coords.blockCoords()).setWidth(0).setHeight(0);
        collisionBoxes.clear();

        Coords c = tailPivot.blockCoords();

        AxisAlignedBB aabb = new AxisAlignedBB(direction.set(c.copy()), 0, 0);
        collisionBoxes.add(aabb);

        boundingBox.include(aabb.getCoords());
        aabb.include(c);

        Direction last = null;
        for (Direction d : tail) {
            d.set(c);
            boundingBox.include(c);

            if (last == d || (last == null && d == direction.opposite())) {
                aabb.grow(d, 1);
            } else {
                aabb.grow(1, 1);
                aabb = new AxisAlignedBB(c, 0, 0);
                collisionBoxes.add(aabb);
            }
            last = d;
        }
        aabb.grow(1, 1);
        boundingBox.grow(1, 1);

        lastDirection = direction;
    }

    @Override
    public void teleport(double x, double y) {
        super.teleport(x, y);
        tailPivot = tailPivot.setX(x).setY(y).blockCoords();
    }

    public void updateDirection(Direction newDirection, Coords at) {

        double off = Math.max(Math.abs(at.getY() - coords.getY()), Math.abs(at.getX() - coords.getX()));
        double delta = Math.abs(off) / speed;

        coords.add(direction.getRx() * -off, direction.getRy() * -off);

        forceDirection(newDirection);
        update0(delta, true);
    }

    public void forceDirection(Direction direction) {
        this.nextDirection.clear();
        this.nextDirection.add(direction);
    }

    public void setDirection(Direction direction) {
        if (direction == this.direction || direction == this.direction.opposite()) {
            return;
        }
        forceDirection(direction);
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

    @Override
    public Collection<AxisAlignedBB> getBoundingBoxes() {
        return collisionBoxes;
    }

    public int getPlayerId() {
        return playerId;
    }

    public SnakeEntity setPlayerId(int playerId) {
        this.playerId = playerId;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public SnakeEntity setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public SnakeEntity setColor(Color color) {
        this.color = color;
        return this;
    }
}

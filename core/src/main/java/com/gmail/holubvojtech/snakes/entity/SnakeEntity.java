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

    private double speed = 0.0085;
    private int lx;
    private int ly;
    private boolean afterForceUpdate = false;

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
                changed = ly < coords.getBlockY();
                break;
            case RIGHT:
                changed = lx < coords.getBlockX();
                break;
        }

        if (changed) {

            if (!nextDirection.isEmpty() && nextDirection.peek() != direction) {
                lastDirection = direction;
                direction = nextDirection.pop();

                coords.setX(Math.round(coords.getX()));
                coords.setY(Math.round(coords.getY()));
            }

            lx = coords.getBlockX();
            ly = coords.getBlockY();
        }

        if (forceChange) {
            afterForceUpdate = true;
            return;
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

        if (afterForceUpdate) {
            afterForceUpdate = false;
            if (direction.isNegative()) {
                updateAABB();
                return;
            }
        }

        if (!tail.isEmpty()) {
            tail.removeLast();
            tail.addFirst(lastDirection.opposite());
        }

        updateAABB();
        lastDirection = direction;
    }

    @Override
    public void teleport(double x, double y) {
        super.teleport(x, y);
    }

    public void grow() {
        if (tail.isEmpty()) {
            tail.add(direction.opposite());
            return;
        }
        Direction last = tail.getLast();
        tail.add(last);
    }

    public void shrink() {
        if (!tail.isEmpty()) {
            tail.removeLast();
        }
    }

    public void updateDirection(Direction newDirection, Coords at) {

        //todo refactor snake movement and direction update

        double dx = coords.getX() - at.getX();
        double dy = coords.getY() - at.getY();

        double off = Math.max(Math.abs(dx), Math.abs(dy));
        System.out.println("off: " + off);
        if (off <= 0) {
            return;
        }
        double delta = Math.abs(off) / speed;

        coords.add(-dx, -dy);

        lastDirection = direction;
        direction = newDirection;
        update0(delta / 2, true);

        if (direction == Direction.UP || direction == Direction.DOWN) {
            coords.setX(Math.round(coords.getX()));
        } else {
            coords.setY(Math.round(coords.getY()));
        }

        if (direction.isNegative() && lastDirection == direction) {
            tailPivot.setX(coords.getBlockX() - direction.getRx()).setY(coords.getBlockY() - direction.getRy());
        } else {
            tailPivot.setX(coords.getBlockX()).setY(coords.getBlockY());
        }

        updateAABB();
        lastDirection = direction;

        forceDirection(newDirection);
    }

    private void updateAABB() {
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

                //shrink aabb by 1 for better collision avoiding
                //(change direction just in front of food)
                //todo better solution?
                if (last == null) {
                    aabb.grow(direction.opposite(), -1);
                    boundingBox.grow(direction.opposite(), -1);
                }
            } else {
                aabb.grow(1, 1);
                aabb = new AxisAlignedBB(c, 0, 0);
                collisionBoxes.add(aabb);
            }
            last = d;
        }
        aabb.grow(1, 1);
        boundingBox.grow(1, 1);
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

    public void __instantGigaMegaChangeDirectionWithoutWaitingForAnything(Direction direction) {
        this.direction = direction;
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

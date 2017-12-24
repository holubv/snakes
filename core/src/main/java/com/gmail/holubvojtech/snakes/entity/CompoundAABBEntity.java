package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AxisAlignedBB;

import java.util.Collection;

public interface CompoundAABBEntity {

    Collection<AxisAlignedBB> getBoundingBoxes();

    static boolean intersects(CompoundAABBEntity entity, AxisAlignedBB aabb) {
        for (AxisAlignedBB box : entity.getBoundingBoxes()) {
            if (box.intersects(aabb)) {
                return true;
            }
        }
        return false;
    }
}

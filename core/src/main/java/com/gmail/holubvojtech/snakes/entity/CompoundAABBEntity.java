package com.gmail.holubvojtech.snakes.entity;

import com.gmail.holubvojtech.snakes.AxisAlignedBB;

import java.util.List;

public interface CompoundAABBEntity {

    List<AxisAlignedBB> getBoundingBoxes();

    static boolean intersects(CompoundAABBEntity entity, AxisAlignedBB aabb) {
        for (AxisAlignedBB box : entity.getBoundingBoxes()) {
            if (box.intersects(aabb)) {
                return true;
            }
        }
        return false;
    }
}

package com.tonmatsu.gl.quadtree.core;

import static org.joml.Math.*;

public class AABB {
    public final Point center;
    public final float halfWidth;
    public final float halfHeight;

    public AABB(Point center, float halfWidth, float halfHeight) {
        this.center = center;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    public float east() {
        return center.x + halfWidth;
    }

    public float west() {
        return center.x - halfWidth;
    }

    public float north() {
        return center.y + halfHeight;
    }

    public float south() {
        return center.y - halfHeight;
    }

    public boolean intersects(AABB aabb) {
        return east() >= aabb.west() && west() <= aabb.east() &&
                north() >= aabb.south() && south() <= aabb.north();
    }

    public boolean intersects(Circle circle) {
        final var x = clamp(west(), east(), circle.center.x);
        final var y = clamp(south(), north(), circle.center.y);
        final var dx = circle.center.x - x;
        final var dy = circle.center.y - y;
        final var d2 = dx * dx + dy * dy;
        final var radius2 = circle.radius * circle.radius;
        return d2 < radius2;
    }
}

package com.tonmatsu.gl.quadtree.core;

import static org.joml.Math.*;

public class Circle {
    public final Point center;
    public final float radius;

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean contains(Point point) {
        return containsPoint(point.x, point.y);
    }

    public boolean containsPoint(float pointX, float pointY) {
        return abs(center.x - pointX) < radius &&
                abs(center.y - pointY) < radius;
    }

    public boolean contains(AABB aabb) {
        final var e = aabb.east();
        final var w = aabb.west();
        final var n = aabb.north();
        final var s = aabb.south();
        return containsPoint(e, n) &&
                containsPoint(e, s) &&
                containsPoint(w, n) &&
                containsPoint(w, s);
    }
}

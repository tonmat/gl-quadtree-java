package com.tonmatsu.gl.quadtree.core;

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
        final var dx = center.x - pointX;
        final var dy = center.y - pointY;
        final var d2 = dx * dx + dy * dy;
        final var radius2 = radius * radius;
        return d2 < radius2;
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

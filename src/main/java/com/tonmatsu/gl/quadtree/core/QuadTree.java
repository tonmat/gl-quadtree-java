package com.tonmatsu.gl.quadtree.core;

public class QuadTree {
    public final AABB boundary;
    public final int maxDepth;
    public final int depth;
    public boolean value;
    public boolean divided;
    public QuadTree nw;
    public QuadTree ne;
    public QuadTree sw;
    public QuadTree se;

    public QuadTree(AABB boundary, int maxDepth) {
        this(boundary, maxDepth, 1);
    }

    private QuadTree(AABB boundary, int maxDepth, int depth) {
        this.boundary = boundary;
        this.maxDepth = maxDepth;
        this.depth = depth;
    }

    public void clear() {
        value = false;
        divided = false;
        nw = null;
        ne = null;
        sw = null;
        se = null;
    }

    public boolean insert(Circle circle) {
        if (circle.contains(boundary)) {
            divided = false;
            value = true;
            return true;
        }

        if (!boundary.intersects(circle))
            return false;

        if (!divided) {
            if (depth == maxDepth) {
                value = true;
                return true;
            }
            divide();
        }

        nw.insert(circle);
        ne.insert(circle);
        sw.insert(circle);
        se.insert(circle);

        return true;
    }

    public boolean remove(Circle circle) {
        if (circle.contains(boundary)) {
            divided = false;
            value = false;
            return true;
        }

        if (!boundary.intersects(circle))
            return false;

        if (!divided) {
            if (depth == maxDepth) {
                value = false;
                return true;
            }
            divide();
        }

        nw.remove(circle);
        ne.remove(circle);
        sw.remove(circle);
        se.remove(circle);

        return true;
    }

    public void normalize(Circle circle) {
        if (!boundary.intersects(circle))
            return;

        if (divided) {
            nw.normalize(circle);
            ne.normalize(circle);
            sw.normalize(circle);
            se.normalize(circle);

            if (nw.divided || ne.divided || sw.divided || se.divided)
                return;

            if (nw.value == ne.value &&
                    nw.value == sw.value &&
                    nw.value == se.value) {
                undivide();
            }
        }
    }

    public int count() {
        if (divided) {
            return nw.count() + ne.count() + sw.count() + se.count() + 1;
        }
        return 1;
    }

    public int countLeaves() {
        if (divided) {
            return nw.countLeaves() + ne.countLeaves() + sw.countLeaves() + se.countLeaves();
        }
        return 1;
    }

    public int countLeavesWithValue() {
        if (divided) {
            return nw.countLeavesWithValue() + ne.countLeavesWithValue() + sw.countLeavesWithValue() + se.countLeavesWithValue();
        }
        return value ? 1 : 0;
    }

    private void divide() {
        final var x = boundary.center.x;
        final var y = boundary.center.y;
        final var w = 0.5f * boundary.halfWidth;
        final var h = 0.5f * boundary.halfHeight;
        final var newDepth = depth + 1;
        nw = new QuadTree(new AABB(new Point(x - w, y + h), w, h), maxDepth, newDepth);
        ne = new QuadTree(new AABB(new Point(x + w, y + h), w, h), maxDepth, newDepth);
        sw = new QuadTree(new AABB(new Point(x - w, y - h), w, h), maxDepth, newDepth);
        se = new QuadTree(new AABB(new Point(x + w, y - h), w, h), maxDepth, newDepth);
        nw.value = value;
        ne.value = value;
        sw.value = value;
        se.value = value;
        value = false;
        divided = true;
    }

    private void undivide() {
        value = nw.value;
        nw = null;
        ne = null;
        sw = null;
        se = null;
        divided = false;
    }
}

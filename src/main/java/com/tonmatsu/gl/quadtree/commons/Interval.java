package com.tonmatsu.gl.quadtree.commons;

public class Interval {
    private final float interval;
    private final Callback callback;
    private float delta;

    public Interval(float interval, Callback callback) {
        this.interval = interval;
        this.callback = callback;
    }

    public void update(float delta) {
        this.delta += delta;
        while (this.delta >= interval) {
            this.delta -= interval;
            callback.callback();
        }
    }

    @FunctionalInterface
    public interface Callback {
        void callback();
    }
}

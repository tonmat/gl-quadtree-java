package com.tonmatsu.gl.quadtree.commons;

public class FPSMeter {
    private final float[] history = new float[100];
    private int size;
    private int index;

    public void update(float delta) {
        if (delta <= 0.0f)
            return;
        final var fps = 1.0f / delta;
        if (size < history.length)
            size++;
        history[index] = fps;
        index = ++index % history.length;
    }

    public float getAverage() {
        var average = 0.0f;
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                average += history[i];
            }
            average /= size;
        }
        return average;
    }
}

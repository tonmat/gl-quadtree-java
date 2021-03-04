package com.tonmatsu.gl.quadtree;

import com.tonmatsu.gl.quadtree.commons.*;
import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.*;
import org.joml.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class QuadTreeApplication {
    private static final String TITLE = "Quad Tree";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private long window;
    private FPSMeter fpsMeter;
    private Interval fpsShowInterval;
    private int[] viewport;
    private Matrix4f projectionMatrix;
    private Vector3f mousePosition;
    private QuadTree quadTree;
    private QuadTreeRenderer renderer;

    public static void main(String[] args) {
        new QuadTreeApplication().run();
    }

    public void run() {
        if (!glfwInit())
            throw new RuntimeException("could not init glfw!");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("could not create window!");
        final var videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode == null)
            throw new RuntimeException("could not get video mode!");
        glfwSetWindowPos(window, (videoMode.width() - WIDTH) / 2, (videoMode.height() - HEIGHT) / 2);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(0);
        glClearColor(0.1f, 0.12f, 0.14f, 1.0f);
        glViewport(0, 0, WIDTH, HEIGHT);
        init();
        glfwShowWindow(window);
        var lastUpdateTime = 0.0;
        while (!glfwWindowShouldClose(window)) {
            final var updateTime = glfwGetTime();
            final var delta = (float) (updateTime - lastUpdateTime);
            lastUpdateTime = updateTime;
            glfwPollEvents();
            update(delta);
            render();
            glfwSwapBuffers(window);
        }
        dispose();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        fpsMeter = new FPSMeter();
        fpsShowInterval = new Interval(1, () -> {
            final var title = String.format("%s - %7.1f FPS - %7d quads trees - %7d leaves - %7d colored leaves",
                    TITLE, fpsMeter.getAverage(), quadTree.count(), quadTree.countLeaves(), quadTree.countLeavesWithValue());
            glfwSetWindowTitle(window, title);
        });
        viewport = new int[]{0, 0, WIDTH, HEIGHT};
        projectionMatrix = new Matrix4f();
        mousePosition = new Vector3f();
        quadTree = new QuadTree(new AABB(new Point(0, 0), 0.5f * HEIGHT, 0.5f * HEIGHT), 10);
        renderer = new QuadTreeRenderer();

        final var w = WIDTH / 2;
        final var h = HEIGHT / 2;
        projectionMatrix.ortho(-w, w, -h, h, 0, 1);
        glOrtho(-w, w, -h, h, 0, 1);
        renderer.setMVP(projectionMatrix);
    }

    private void update(float delta) {
        fpsMeter.update(delta);
        fpsShowInterval.update(delta);
        try (final var stack = stackPush()) {
            final var xpos = stack.callocDouble(1);
            final var ypos = stack.callocDouble(1);
            glfwGetCursorPos(window, xpos, ypos);
            projectionMatrix.unproject((float) xpos.get(), HEIGHT - (float) ypos.get(), 0, viewport, mousePosition);
        }
        if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
            quadTree.clear();
            renderer.update(quadTree);
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            final var circle = new Circle(new Point(mousePosition.x, mousePosition.y), 32);
            quadTree.insert(circle);
            quadTree.normalize(circle);
            renderer.update(quadTree);
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            final var circle = new Circle(new Point(mousePosition.x, mousePosition.y), 32);
            quadTree.remove(circle);
            quadTree.normalize(circle);
            renderer.update(quadTree);
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        renderer.render();
    }

    private void dispose() {
        renderer.dispose();
    }
}

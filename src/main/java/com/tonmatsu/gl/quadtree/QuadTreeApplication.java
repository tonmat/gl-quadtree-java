package com.tonmatsu.gl.quadtree;

import static org.joml.Math.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.tonmatsu.gl.quadtree.commons.*;
import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.*;
import org.joml.*;
import org.lwjgl.opengl.*;

public class QuadTreeApplication {
    private static final String TITLE = "Quad Tree";
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080-64;
    private static final int QUADTREE_DEPTH = 12;
    private static final int MAX_ZOOM = 16;
    private long window;
    private FPSMeter fpsMeter;
    private Interval fpsShowInterval;
    private int[] viewport;
    private Vector3f cameraPosition;
    private Vector3f cameraPositionTarget;
    private float cameraZoom;
    private float cameraZoomTarget;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f vpMatrix;
    private Vector3f mousePosition;
    private Vector3f mouseLastPosition;
    private Vector3f mouseWorldPosition;
    private Vector3f mouseWorldLastPosition;
    private QuadTree quadTree;
    private QuadTreeRenderer renderer;
    private final Vector3f vec3 = new Vector3f();

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
        glfwSetScrollCallback(window, this::handleScrollCallback);
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
        glfwSetScrollCallback(window, null);
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
        cameraPosition = new Vector3f();
        cameraPositionTarget = new Vector3f();
        cameraZoomTarget = 1;
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        vpMatrix = new Matrix4f();
        mousePosition = new Vector3f();
        mouseLastPosition = new Vector3f();
        mouseWorldPosition = new Vector3f();
        mouseWorldLastPosition = new Vector3f();
        quadTree = new QuadTree(new AABB(new Point(1, 1), 0.5f * HEIGHT - 2, 0.5f * HEIGHT - 2), QUADTREE_DEPTH);
        renderer = new QuadTreeRenderer();
        renderer.update(quadTree);

        final var w = WIDTH / 2;
        final var h = HEIGHT / 2;
        projectionMatrix.setOrtho(-w, w, -h, h, -1000.0f, 1000.0f);
    }

    private void handleScrollCallback(long window, double xoffset, double yoffset) {
        if (yoffset < 0)
            if (cameraZoomTarget <= 1)
                return;
        if (yoffset > 0) {
            if (cameraZoomTarget >= MAX_ZOOM)
                return;
        }
        cameraZoomTarget = clamp(1, MAX_ZOOM, cameraZoomTarget + 0.5f * (float) yoffset * cameraZoomTarget);
    }

    private void update(float delta) {
        fpsMeter.update(delta);
        fpsShowInterval.update(delta);

        try (final var stack = stackPush()) {
            final var xpos = stack.mallocDouble(1);
            final var ypos = stack.mallocDouble(1);
            glfwGetCursorPos(window, xpos, ypos);
            mousePosition.set((float) xpos.get(), HEIGHT - (float) ypos.get(), 0.0f);
        }
        vpMatrix.unproject(mousePosition, viewport, mouseWorldPosition);

        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_MIDDLE) == GLFW_PRESS) {
            vec3.set(mousePosition).sub(mouseLastPosition);
            cameraPositionTarget.fma(-1.0f / cameraZoom, vec3);
        }

        if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
            cameraPositionTarget.zero();
            cameraZoomTarget = 1;
            quadTree.clear();
            renderer.update(quadTree);
        }
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
            cameraPositionTarget.zero();
            cameraZoomTarget = 1;
            quadTree.clear();
            quadTree.color = new Vector3f(0.5f, 0.7f, 0.2f);
            renderer.update(quadTree);
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            final var circle = new Circle(new Point(mouseWorldPosition.x, mouseWorldPosition.y), 32.0f / cameraZoom);
            quadTree.insert(circle);
            quadTree.normalize(circle);
            renderer.update(quadTree);
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            final var circle = new Circle(new Point(mouseWorldPosition.x, mouseWorldPosition.y), 32 / cameraZoom);
            quadTree.remove(circle);
            quadTree.normalize(circle);
            renderer.update(quadTree);
        }

        cameraPosition.lerp(cameraPositionTarget, clamp(0, 1, 16.0f * delta));
        cameraZoom = lerp(cameraZoom, cameraZoomTarget, clamp(0, 1, 16.0f * delta));
        viewMatrix.identity()
                .scale(cameraZoom)
                .translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        projectionMatrix.mul(viewMatrix, vpMatrix);
        renderer.setMVP(vpMatrix);

        mouseLastPosition.set(mousePosition);
        mouseWorldLastPosition.set(mouseWorldPosition);
    }

    private void render() {
        renderer.render();
    }

    private void dispose() {
        renderer.dispose();
    }
}

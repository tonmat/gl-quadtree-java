package com.tonmatsu.gl.quadtree.renderer;

import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.gl.*;
import org.joml.*;

import java.nio.*;

import static com.tonmatsu.gl.quadtree.renderer.gl.VertexAttribute.*;
import static org.joml.Math.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class QuadTreeVertexArray {
    private static final Vector3f COLOR = new Vector3f(0.14f, 0.16f, 0.18f);
    private final VertexArray vao;
    private final VertexBuffer vbo;
    private final IndexBuffer ibo;
    private ByteBuffer vertices;
    private int capacity;
    private int count;

    public QuadTreeVertexArray() {
        vbo = new VertexBuffer();
        ibo = new IndexBuffer();
        vao = new VertexArray();
        vao.bindVertexBuffer(vbo, VEC3);
        vao.bindIndexBuffer(ibo);
        recreate(8192);
    }

    public void dispose() {
        vao.dispose();
        vbo.dispose();
        ibo.dispose();
        memFree(vertices);
    }

    public void update(QuadTree quadTree) {
        count = quadTree.countLeaves();
        if (count > capacity) {
            var newCapacity = capacity;
            while (count > newCapacity)
                newCapacity *= 2;
            recreate(newCapacity);
        }

        vertices.clear();
        add(quadTree);
        vertices.flip();
        vbo.update(0L, vertices);
    }

    public void draw() {
        vao.bind();
        glDrawElements(GL_LINES, count * 8, GL_UNSIGNED_INT, 0L);
        vao.unbind();
    }

    private void recreate(int newCapacity) {
        if (vertices != null)
            memFree(vertices);

        vertices = memAlloc(newCapacity * 4 * 12);
        vbo.create(vertices.limit(), GL_DYNAMIC_DRAW);

        final var indexData = memAlloc(newCapacity * 8 * 4);
        for (int i = 0; i < newCapacity; i++) {
            final var v = i * 4;
            indexData.putInt(v);
            indexData.putInt(v + 1);
            indexData.putInt(v + 1);
            indexData.putInt(v + 2);
            indexData.putInt(v + 2);
            indexData.putInt(v + 3);
            indexData.putInt(v + 3);
            indexData.putInt(v);
        }
        indexData.flip();
        ibo.create(indexData.limit(), GL_STATIC_DRAW);
        ibo.update(0, indexData);
        memFree(indexData);

        capacity = newCapacity;
    }

    private void add(QuadTree quadTree) {
        if (!quadTree.divided) {
            var color = COLOR;
            var zIndex = 0.0f;
            if (quadTree.color != null) {
                color = quadTree.color;
                zIndex = 1.0f;
            }
            final var e = quadTree.boundary.east();
            final var w = quadTree.boundary.west();
            final var n = quadTree.boundary.north();
            final var s = quadTree.boundary.south();
            final var c = packColor(color.x, color.y, color.z, zIndex);
            vertices.putFloat(w).putFloat(s).putFloat(c);
            vertices.putFloat(e).putFloat(s).putFloat(c);
            vertices.putFloat(e).putFloat(n).putFloat(c);
            vertices.putFloat(w).putFloat(n).putFloat(c);
            return;
        }

        add(quadTree.ne);
        add(quadTree.nw);
        add(quadTree.sw);
        add(quadTree.se);
    }

    private static float packColor(float r, float g, float b, float zIndex) {
        return floor(63.0f * r) / 64.0f +
                floor(63.0f * g) / 4096.0f +
                floor(63.0f * b) / 262144.0f+
                floor(31.0f * zIndex) / 8388608.0f;
    }
}

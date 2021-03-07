package com.tonmatsu.gl.quadtree.renderer;

import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.gl.*;

import java.nio.*;

import static com.tonmatsu.gl.quadtree.renderer.gl.VertexAttribute.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class QuadTreeVertexArray {
    private final VertexBuffer vbo;
    private final IndexBuffer ibo1;
    private final VertexArray vao1;
    private final IndexBuffer ibo2;
    private final VertexArray vao2;
    private ByteBuffer vertices;
    private int capacity;
    private int count;

    public QuadTreeVertexArray() {
        vbo = new VertexBuffer();
        ibo1 = new IndexBuffer();
        vao1 = new VertexArray();
        vao1.bindVertexBuffer(vbo, VEC3);
        vao1.bindIndexBuffer(ibo1);
        ibo2 = new IndexBuffer();
        vao2 = new VertexArray();
        vao2.bindVertexBuffer(vbo, VEC3);
        vao2.bindIndexBuffer(ibo2);
        recreate(8192);
    }

    public void dispose() {
        vbo.dispose();
        ibo1.dispose();
        vao1.dispose();
        ibo2.dispose();
        vao2.dispose();
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

    public void drawLines() {
        vao1.bind();
        glDrawElements(GL_LINES, count * 8, GL_UNSIGNED_INT, NULL);
        vao1.unbind();
    }

    public void drawTriangles() {
        vao2.bind();
        glDrawElements(GL_TRIANGLES, count * 6, GL_UNSIGNED_INT, NULL);
        vao2.unbind();
    }

    private void recreate(int newCapacity) {
        if (vertices != null)
            memFree(vertices);

        vertices = memAlloc(newCapacity * 4 * 12);
        vbo.create(vertices.limit(), GL_DYNAMIC_DRAW);

        final var indexData1 = memAlloc(newCapacity * 8 * 4);
        for (int i = 0; i < newCapacity; i++) {
            final var v = i * 4;
            indexData1.putInt(v);
            indexData1.putInt(v + 1);
            indexData1.putInt(v + 1);
            indexData1.putInt(v + 2);
            indexData1.putInt(v + 2);
            indexData1.putInt(v + 3);
            indexData1.putInt(v + 3);
            indexData1.putInt(v);
        }
        indexData1.flip();
        ibo1.create(indexData1.limit(), GL_STATIC_DRAW);
        ibo1.update(0, indexData1);
        memFree(indexData1);

        final var indexData2 = memAlloc(newCapacity * 6 * 4);
        for (int i = 0; i < newCapacity; i++) {
            final var v = i * 4;
            indexData2.putInt(v);
            indexData2.putInt(v + 1);
            indexData2.putInt(v + 2);
            indexData2.putInt(v + 2);
            indexData2.putInt(v + 3);
            indexData2.putInt(v);
        }
        indexData2.flip();
        ibo2.create(indexData2.limit(), GL_STATIC_DRAW);
        ibo2.update(0, indexData2);
        memFree(indexData2);

        capacity = newCapacity;
    }

    private void add(QuadTree quadTree) {
        if (!quadTree.divided) {
            final var e = quadTree.boundary.east();
            final var w = quadTree.boundary.west();
            final var n = quadTree.boundary.north();
            final var s = quadTree.boundary.south();
            final var v = quadTree.value ? 1.0f : 0.0f;
            vertices.putFloat(w).putFloat(s).putFloat(v);
            vertices.putFloat(e).putFloat(s).putFloat(v);
            vertices.putFloat(e).putFloat(n).putFloat(v);
            vertices.putFloat(w).putFloat(n).putFloat(v);
            return;
        }

        add(quadTree.ne);
        add(quadTree.nw);
        add(quadTree.sw);
        add(quadTree.se);
    }
}

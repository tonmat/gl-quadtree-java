package com.tonmatsu.gl.quadtree.renderer;

import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.gl.*;

import static com.tonmatsu.gl.quadtree.renderer.gl.VertexAttribute.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TextureVertexArray {
    private final VertexArray vao;
    private final VertexBuffer vbo;
    private final IndexBuffer ibo;

    public TextureVertexArray() {
        vbo = new VertexBuffer();
        ibo = new IndexBuffer();
        vao = new VertexArray();
        vao.bindVertexBuffer(vbo, VEC4);
        vao.bindIndexBuffer(ibo);
        create();
    }

    public void dispose() {
        vao.dispose();
        vbo.dispose();
        ibo.dispose();
    }

    public void update(QuadTree quadTree) {
        final var vertexData = memAlloc(4 * 16);
        vertexData.putFloat(quadTree.boundary.west()).putFloat(quadTree.boundary.south()).putFloat(0).putFloat(1);
        vertexData.putFloat(quadTree.boundary.east()).putFloat(quadTree.boundary.south()).putFloat(1).putFloat(1);
        vertexData.putFloat(quadTree.boundary.east()).putFloat(quadTree.boundary.north()).putFloat(1).putFloat(0);
        vertexData.putFloat(quadTree.boundary.west()).putFloat(quadTree.boundary.north()).putFloat(0).putFloat(0);
        vertexData.flip();
        vbo.update(NULL, vertexData);
        memFree(vertexData);
    }

    public void draw() {
        vao.bind();
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, NULL);
        vao.unbind();
    }

    private void create() {
        vbo.create(4 * 16, GL_DYNAMIC_DRAW);

        final var indexData = memAlloc(6 * 4);
        indexData.putInt(0);
        indexData.putInt(1);
        indexData.putInt(2);
        indexData.putInt(2);
        indexData.putInt(3);
        indexData.putInt(0);
        indexData.flip();
        ibo.create(indexData.limit(), GL_STATIC_DRAW);
        ibo.update(0, indexData);
        memFree(indexData);
    }
}

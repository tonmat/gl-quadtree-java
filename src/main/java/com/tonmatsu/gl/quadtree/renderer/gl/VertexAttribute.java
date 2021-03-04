package com.tonmatsu.gl.quadtree.renderer.gl;

import static org.lwjgl.opengl.GL11.*;

public class VertexAttribute {
    public static final VertexAttribute BOOL = new VertexAttribute(1, GL_UNSIGNED_BYTE, false, 1);
    public static final VertexAttribute FLOAT = new VertexAttribute(1, GL_FLOAT, false, 4);
    public static final VertexAttribute VEC2 = new VertexAttribute(2, GL_FLOAT, false, 8);
    public static final VertexAttribute VEC3 = new VertexAttribute(3, GL_FLOAT, false, 12);
    public static final VertexAttribute VEC4 = new VertexAttribute(4, GL_FLOAT, false, 16);
    public final int size;
    public final int type;
    public final boolean normalized;
    public final int bytes;

    public VertexAttribute(int size, int type, boolean normalized, int bytes) {
        this.size = size;
        this.type = type;
        this.normalized = normalized;
        this.bytes = bytes;
    }
}

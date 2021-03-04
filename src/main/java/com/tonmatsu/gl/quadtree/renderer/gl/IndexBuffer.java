package com.tonmatsu.gl.quadtree.renderer.gl;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {
    private static IndexBuffer binded;
    private final int buffer;

    public IndexBuffer() {
        buffer = glGenBuffers();
    }

    public void dispose() {
        glDeleteBuffers(buffer);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
        binded = null;
    }

    public void create(int size, int usage) {
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, usage);
        unbind();
    }

    public void update(long offset, ByteBuffer data) {
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data);
        unbind();
    }
}

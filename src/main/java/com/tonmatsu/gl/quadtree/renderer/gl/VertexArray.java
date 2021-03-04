package com.tonmatsu.gl.quadtree.renderer.gl;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    private static VertexArray binded;
    private final int array;
    private int attributeIndex;

    public VertexArray() {
        array = glGenVertexArrays();
    }

    public void dispose() {
        glDeleteVertexArrays(array);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindVertexArray(array);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindVertexArray(GL_NONE);
        binded = null;
    }

    public void bindVertexBuffer(VertexBuffer vbo, VertexAttribute... attributes) {
        var stride = 0;
        for (final var attribute : attributes)
            stride += attribute.bytes;

        bind();
        vbo.bind();
        var pointer = 0L;
        for (final var attribute : attributes) {
            switch (attribute.type) {
                case GL_BYTE:
                case GL_SHORT:
                case GL_INT:
                case GL_UNSIGNED_BYTE:
                case GL_UNSIGNED_SHORT:
                case GL_UNSIGNED_INT:
                    glVertexAttribIPointer(attributeIndex, attribute.size, attribute.type, stride, pointer);
                default:
                    glVertexAttribPointer(attributeIndex, attribute.size, attribute.type, attribute.normalized, stride, pointer);
                    break;
            }

            glEnableVertexAttribArray(attributeIndex);
            pointer += attribute.bytes;
            attributeIndex++;
        }
        vbo.unbind();
        unbind();
    }

    public void bindIndexBuffer(IndexBuffer ibo) {
        bind();
        ibo.bind();
        unbind();
        ibo.unbind();
    }
}

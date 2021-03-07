package com.tonmatsu.gl.quadtree.renderer.gl;

import com.tonmatsu.gl.quadtree.utils.*;

import java.nio.*;

import static java.util.Objects.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Texture {
    private static Texture binded;
    private final int texture;

    public Texture() {
        texture = glGenTextures();
    }

    public void dispose() {
        glDeleteTextures(texture);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindTexture(GL_TEXTURE_2D, texture);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        binded = null;
    }

    public void setParameteri(int name, int param) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, name, param);
        unbind();
    }

    public void create(int internalFormat, int format, int type, String asset) {
        final var buffer = AssetUtils.getBuffer(asset);
        try (final var stack = stackPush()) {
            final var x = stack.mallocInt(1);
            final var y = stack.mallocInt(1);
            final var c = stack.mallocInt(1);
            final var pixels = requireNonNull(stbi_load_from_memory(buffer, x, y, c, 0));
            bind();
            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, x.get(0), y.get(0), 0, format, type, pixels);
            unbind();
            stbi_image_free(pixels);
        }
    }

    public void create(int internalFormat, int width, int height, int format, int type) {
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, NULL);
        unbind();
    }

    public void update(int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
        bind();
        glTexSubImage2D(GL_TEXTURE_2D, 0, xoffset, yoffset, width, height, format, type, pixels);
        unbind();
    }
}

package com.tonmatsu.gl.quadtree.renderer.gl;

import com.tonmatsu.gl.quadtree.utils.*;
import org.joml.*;

import java.util.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;

public class ShaderProgram {
    private static ShaderProgram binded;
    private final int program;
    private final HashMap<String, Integer> uniformsLocations;

    public ShaderProgram() {
        program = glCreateProgram();
        uniformsLocations = new HashMap<>();
    }

    public void dispose() {
        glDeleteProgram(program);
    }

    public boolean bind() {
        if (binded == this)
            return false;
        glUseProgram(program);
        binded = this;
        return true;
    }

    public boolean unbind() {
        if (binded == null)
            return false;
        glUseProgram(GL_NONE);
        binded = null;
        return true;
    }

    public void attachShader(int type, String asset) {
        final var source = AssetUtils.getString(asset);
        final var shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        final var status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_TRUE) {
            glAttachShader(program, shader);
        } else {
            System.err.println("could not compile shader " + asset);
            System.err.println(glGetShaderInfoLog(shader));
        }
        glDeleteShader(shader);
    }

    public void link() {
        glLinkProgram(program);
        final var status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == GL_TRUE)
            return;
        System.err.println("could not link program " + program);
        System.err.println(glGetProgramInfoLog(program));
    }

    public int getUniformLocation(String name) {
        var location = uniformsLocations.get(name);
        if (location == null) {
            location = glGetUniformLocation(program, name);
            if (location == -1)
                System.err.println("could not get uniform location " + name);
            uniformsLocations.put(name, location);
        }
        return location;
    }

    public void setUniform1b(String name, boolean b) {
        final var binded = bind();
        glUniform1i(getUniformLocation(name), b ? GL_TRUE : GL_FALSE);
        if (binded) unbind();
    }

    public void setUniform1i(String name, int i) {
        final var binded = bind();
        glUniform1i(getUniformLocation(name), i);
        if (binded) unbind();
    }

    public void setUniform1f(String name, float f) {
        final var binded = bind();
        glUniform1f(getUniformLocation(name), f);
        if (binded) unbind();
    }

    public void setUniform2f(String name, Vector2f vec2) {
        final var binded = bind();
        glUniform2f(getUniformLocation(name), vec2.x, vec2.y);
        if (binded) unbind();
    }

    public void setUniform3f(String name, Vector3f vec3) {
        final var binded = bind();
        glUniform3f(getUniformLocation(name), vec3.x, vec3.y, vec3.z);
        if (binded) unbind();
    }

    public void setUniform4f(String name, Vector4f vec4) {
        final var binded = bind();
        glUniform4f(getUniformLocation(name), vec4.x, vec4.y, vec4.z, vec4.w);
        if (binded) unbind();
    }

    public void setUniformMatrix4f(String name, Matrix4f mat4) {
        final var binded = bind();
        try (final var stack = stackPush()) {
            glUniformMatrix4fv(getUniformLocation(name), false, mat4.get(stack.mallocFloat(16)));
        }
        if (binded) unbind();
    }
}

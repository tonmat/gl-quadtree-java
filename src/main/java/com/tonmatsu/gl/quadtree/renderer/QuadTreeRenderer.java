package com.tonmatsu.gl.quadtree.renderer;

import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.gl.*;
import org.joml.*;

import static org.lwjgl.opengl.GL20.*;

public class QuadTreeRenderer {
    private final Texture texture;
    private final ShaderProgram shaderProgram1;
    private final ShaderProgram shaderProgram2;
    private final QuadTreeVertexArray vao1;
    private final TextureVertexArray vao2;
    private final Matrix4f mvp;

    public QuadTreeRenderer() {
        texture = new Texture();
        texture.setParameteri(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameteri(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        texture.setParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT);
        texture.setParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT);
        texture.create(GL_RGB8, GL_RGB, GL_UNSIGNED_BYTE, "textures/stone-1024.jpg");
        shaderProgram1 = new ShaderProgram();
        shaderProgram1.attachShader(GL_VERTEX_SHADER, "shaders/quad.vs.glsl");
        shaderProgram1.attachShader(GL_FRAGMENT_SHADER, "shaders/quad.fs.glsl");
        shaderProgram1.link();
        shaderProgram2 = new ShaderProgram();
        shaderProgram2.attachShader(GL_VERTEX_SHADER, "shaders/texture.vs.glsl");
        shaderProgram2.attachShader(GL_FRAGMENT_SHADER, "shaders/texture.fs.glsl");
        shaderProgram2.link();
        vao1 = new QuadTreeVertexArray();
        vao2 = new TextureVertexArray();
        mvp = new Matrix4f();
    }

    public void dispose() {
        shaderProgram1.dispose();
        vao1.dispose();
        vao2.dispose();
    }

    public void setMVP(Matrix4f mvp) {
        this.mvp.set(mvp);
    }

    public void update(QuadTree quadTree) {
        vao1.update(quadTree);
        vao2.update(quadTree);
    }

    public void render() {
        glStencilMask(0xFF);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        shaderProgram1.bind();
        shaderProgram1.setUniformMatrix4f("u_mvp", mvp);
        shaderProgram1.setUniform1b("u_just_value", true);
        vao1.drawTriangles();
        shaderProgram1.unbind();

        glColorMask(true, true, true, true);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
        glStencilMask(0x00);
        texture.bind();
        shaderProgram2.bind();
        shaderProgram2.setUniformMatrix4f("u_mvp", mvp);
        shaderProgram2.setUniform1f("u_color", 0.1f);
        vao2.draw();
        glStencilFunc(GL_EQUAL, 1, 0xFF);
        shaderProgram2.setUniform1f("u_color", 1.0f);
        vao2.draw();
        shaderProgram2.unbind();
        texture.unbind();
        glDisable(GL_STENCIL_TEST);

        glEnable(GL_DEPTH_TEST);
        shaderProgram1.bind();
        shaderProgram1.setUniformMatrix4f("u_mvp", mvp);
        shaderProgram1.setUniform1b("u_just_value", false);
        vao1.drawLines();
        shaderProgram1.unbind();
        glDisable(GL_DEPTH_TEST);
    }
}

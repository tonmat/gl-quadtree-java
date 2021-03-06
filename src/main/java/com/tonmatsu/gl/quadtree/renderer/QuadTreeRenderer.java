package com.tonmatsu.gl.quadtree.renderer;

import com.tonmatsu.gl.quadtree.core.*;
import com.tonmatsu.gl.quadtree.renderer.gl.*;
import org.joml.*;

import static org.lwjgl.opengl.GL20.*;

public class QuadTreeRenderer {
    private final ShaderProgram shaderProgram;
    private final QuadTreeVertexArray vao;
    private final Matrix4f mvp;

    public QuadTreeRenderer() {
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(GL_VERTEX_SHADER, "shaders/quad.vs.glsl");
        shaderProgram.attachShader(GL_FRAGMENT_SHADER, "shaders/quad.fs.glsl");
        shaderProgram.link();
        vao = new QuadTreeVertexArray();
        mvp = new Matrix4f();
    }

    public void dispose() {
        shaderProgram.dispose();
        vao.dispose();
    }

    public void setMVP(Matrix4f mvp) {
        this.mvp.set(mvp);
    }

    public void update(QuadTree quadTree) {
        vao.update(quadTree);
    }

    public void render() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shaderProgram.bind();
        shaderProgram.setUniformMatrix4f("u_mvp", mvp);
        vao.draw();
        shaderProgram.unbind();
        glDisable(GL_DEPTH_TEST);
    }
}

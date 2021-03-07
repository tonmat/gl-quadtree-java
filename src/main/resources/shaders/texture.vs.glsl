#version 330

layout (location = 0) in vec4 a_pos_tc;

uniform mat4 u_mvp;

out vec2 v_texcoords;

void main() {
    gl_Position = u_mvp * vec4(a_pos_tc.xy, 0, 1);
    v_texcoords = a_pos_tc.zw;
}
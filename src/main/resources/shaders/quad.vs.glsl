#version 330

layout (location = 0) in vec3 a_position;

uniform mat4 u_mvp;

out float v_value;

void main() {
    gl_Position = u_mvp * vec4(a_position, 1.0);
    v_value = a_position.z;
}
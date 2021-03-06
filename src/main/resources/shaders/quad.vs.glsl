#version 330

layout (location = 0) in vec3 a_pos_color;

uniform mat4 u_mvp;

out vec4 v_color;

vec4 unpackColor(float f) {
    vec4 color;
    float t;

    t = f * 64.0;
    color.r = floor(t);
    t = (t - color.r) * 64.0;
    color.g = floor(t);
    t = (t - color.g) * 64.0;
    color.b = floor(t);
    t = (t - color.b) * 32.0;
    color.a = floor(t);

    color.r = color.r / 63.0;
    color.g = color.g / 63.0;
    color.b = color.b / 63.0;
    color.a = color.a / 31.0;

    return color;
}

void main() {
    gl_Position = u_mvp * vec4(a_pos_color.xy, 0.0, 1.0);
    v_color = unpackColor(a_pos_color.z);
}
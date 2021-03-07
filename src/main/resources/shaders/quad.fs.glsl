#version 330

in float v_value;

uniform bool u_just_value;

out vec4 f_color;

void main() {
    if (v_value == 0.0) {
        if (u_just_value) {
            discard;
        }
        f_color = vec4(0.1, 0.12, 0.14, 1.0);
    } else {
        f_color = vec4(0.2, 0.24, 0.28, 1.0);
    }
}
#version 330

in vec2 v_texcoords;

uniform sampler2D u_texture;
uniform float u_color;

out vec4 f_color;

void main() {
    f_color = texture(u_texture, v_texcoords);
    f_color.rgb = f_color.rgb * u_color;
}
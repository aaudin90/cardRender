#version 300 es
precision mediump float;
uniform vec4 vColor;
out vec4 fragColor;

uniform sampler2D u_Texture;
in vec2 vTexturePosition;

void main()
{
    fragColor = texture(u_Texture, vTexturePosition);
}
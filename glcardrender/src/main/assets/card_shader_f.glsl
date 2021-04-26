#version 300 es
precision mediump float;
out vec4 fragColor;

uniform sampler2D u_Texture;
in vec2 vTexturePosition;
in vec4 vNormals;

void main()
{
    float ambientStrength = 0.6;
    vec4 ambient = ambientStrength * vec4(1.0);

    fragColor = ambient * texture(u_Texture, vTexturePosition);
}
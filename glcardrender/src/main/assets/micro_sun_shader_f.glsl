#version 300 es
precision mediump float;

out vec4 fragColor;

void main()
{
    vec4 lightColor = vec4(1.0, 1.0, 0, 1.0);
    fragColor = lightColor;
}
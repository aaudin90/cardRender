#version 300 es
uniform mat4 uMVPMatrix;
in vec4 vPosition;
in vec2 aTexturePosition;

out vec2 vTexturePosition;

void main()
{
    gl_Position = uMVPMatrix * vPosition;
    vTexturePosition = aTexturePosition;
}
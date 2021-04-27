#version 300 es
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
in vec4 a_Position;

void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * a_Position;
}
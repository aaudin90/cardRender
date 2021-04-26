#version 300 es
uniform mat4 uMVPMatrix;
in vec4 a_Position;
in vec2 a_TexPosition;
in vec4 a_Normals;

out vec2 vTexturePosition;
out vec4 vNormals;

void main()
{
    gl_Position = uMVPMatrix * a_Position;
    vTexturePosition = a_TexPosition;
}
#version 300 es
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 normalizedModelMatrix;
uniform mat4 projectionMatrix;
uniform vec3 vLightPos;
in vec4 a_Position;
in vec2 a_TexPosition;
in vec3 a_Normals;

out vec2 vTexturePosition;
out vec3 vNormals;
out vec3 FragPos;
out vec3 LightPos;

void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * a_Position;

    FragPos = vec3(viewMatrix * modelMatrix * a_Position);
    vNormals = mat3(normalizedModelMatrix) * a_Normals;
    vTexturePosition = a_TexPosition;
    LightPos = vec3(viewMatrix * vec4(vLightPos, 1.0));
}
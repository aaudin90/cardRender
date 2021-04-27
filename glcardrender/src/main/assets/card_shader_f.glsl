#version 300 es
precision mediump float;
uniform sampler2D u_Texture;
uniform vec3 lightPos;
in vec2 vTexturePosition;
in vec3 vNormals;
in vec3 FragPos;

out vec4 fragColor;

void main()
{
    vec4 lightColor = vec4(1.0);
    float ambientStrength = 0.5;
    vec4 ambient = ambientStrength * lightColor;

    vec3 norm = normalize(vNormals);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec4 diffuse = diff * lightColor;

    fragColor = (ambient + diffuse) * texture(u_Texture, vTexturePosition);
}
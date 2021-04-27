#version 300 es
precision mediump float;
uniform sampler2D u_Texture;
uniform vec3 lightPos;
uniform vec3 viewPos;
in vec2 vTexturePosition;
in vec3 vNormals;
in vec3 FragPos;

out vec4 fragColor;

void main()
{
    vec4 lightColor = vec4(1.0);
    float ambientStrength = 0.2;
    vec4 ambient = ambientStrength * lightColor;

    vec3 norm = normalize(vNormals);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec4 diffuse = diff * lightColor;

    float specularStrength = 0.9;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.0);
    vec4 specular = specularStrength * spec * lightColor;

    fragColor = (ambient +diffuse + specular) * texture(u_Texture, vTexturePosition);
}
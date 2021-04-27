#version 300 es
precision mediump float;
uniform sampler2D u_Texture;
uniform sampler2D u_SpecularMap;
uniform vec3 viewPos;
in vec2 vTexturePosition;
in vec3 vNormals;
in vec3 FragPos;
in vec3 LightPos;

out vec4 fragColor;

void main()
{
    vec4 lightColor = vec4(1.0);
    float ambientStrength = 0.4;
    vec4 ambient = ambientStrength * lightColor * texture(u_Texture, vTexturePosition);

    float diffuseStrength = 0.7;
    vec3 norm = normalize(vNormals);
    vec3 lightDir = normalize(LightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec4 diffuse = diff * lightColor * diffuseStrength * texture(u_Texture, vTexturePosition);

    float specularStrength = 0.9;
    vec3 viewDir = normalize(-FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8.0);
    vec4 specular = specularStrength * spec * lightColor * texture(u_SpecularMap, vTexturePosition);

    fragColor = ambient + diffuse + specular;
}
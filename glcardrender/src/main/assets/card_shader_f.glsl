#version 300 es
precision mediump float;
uniform sampler2D u_Texture;
uniform sampler2D u_SpecularMap;
uniform vec3 viewPos;

uniform float ambientStrength;
uniform vec3 ambientLightColor;

uniform float diffuseStrength;
uniform vec3 diffuseLightColor;

uniform float specularMapStrength;
uniform vec3 specularMapLightColor;

uniform float specularTextureStrength;
uniform vec3 specularTextureLightColor;

in vec2 vTexturePosition;
in vec3 vNormals;
in vec3 FragPos;
in vec3 LightPos;

out vec4 fragColor;

void main()
{
    vec4 ambient = ambientStrength * vec4(ambientLightColor, 0.0) * texture(u_Texture, vTexturePosition);

    vec3 norm = normalize(vNormals);
    vec3 lightDir = normalize(LightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec4 diffuse = diff * vec4(diffuseLightColor, 0.0) * diffuseStrength * texture(u_Texture, vTexturePosition);

    vec3 viewDir = normalize(-FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float specMap = pow(max(dot(viewDir, reflectDir), 0.0), 16.0);
    vec4 specularMap = specMap * specularMapStrength * vec4(specularMapLightColor, 0.0) * texture(u_SpecularMap, vTexturePosition);

    float specTexture = pow(max(dot(viewDir, reflectDir), 0.0), 8.0);
    vec4 specularTexture = specTexture * specularTextureStrength * vec4(specularTextureLightColor, 0.0) * texture(u_Texture, vTexturePosition);

    fragColor = ambient + diffuse + specularMap + specularTexture;
}
#version 330

in vec2 exTextCoords;
in vec3 exColor;
in vec3 exNormal;
in vec3 fragPos;

out vec4 fragColor;

const int MAX_POINT_LIGHT = 1;

struct Material {
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float ambientStrength;
	float diffuseStrenght;
	float specularStrenght;
	int hasTexture;
	int specularExponent;
};

struct PointLight {
	vec3 color;
	vec3 position;
};

struct DirectionalLight {
	vec3 direction;
	vec3 color;
	float intensity;
};

uniform sampler2D texture0;
uniform Material material;
uniform PointLight pointLight[MAX_POINT_LIGHT];
uniform vec3 cameraPos;
uniform DirectionalLight directionalLight;
uniform int nbComposedMeshWidth, nbComposedMeshHeight;

// Diffuse + Specular color of point light
vec4 calculPointLightColor(int index) {
	// Diffuse
	vec3 dirFragLight = normalize(pointLight[index].position - fragPos);
	vec3 normal = normalize(exNormal);
	float angle = max(dot(normal, dirFragLight), 0.0);
	vec4 diffuse = vec4(angle * material.diffuse, 1.0) * vec4(pointLight[index].color, 1.0);
	
	// Specular
	vec3 dirFragCam = normalize(cameraPos - fragPos);
	vec3 dirReflect = reflect(-dirFragLight, normal);
	angle = pow(max(dot(dirReflect, dirFragCam), 0.0), material.specularExponent);
	vec4 specular = vec4(angle * material.specular, 1.0) * vec4(pointLight[index].color, 1.0);
	
	return (diffuse * material.diffuseStrenght) + (specular * material.specularStrenght);
}

// Diffuse + Specular color of directional light
vec4 calculDirectionalLightColor() {
	// Diffuse
	vec3 dirFragLight = normalize(directionalLight.direction); // weird
	vec3 normal = normalize(exNormal);
	float angle = max(dot(normal, dirFragLight), 0.0);
	vec4 diffuse = vec4(angle * material.diffuse, 1.0) * vec4(directionalLight.color, 1.0);
	
	// Specular
	vec3 dirFragCam = normalize(cameraPos - fragPos);
	vec3 dirReflect = reflect(dirFragLight, normal);
	angle = pow(max(dot(dirReflect, dirFragCam), 0.0), material.specularExponent);
	vec4 specular = vec4(angle * material.specular, 1.0) * vec4(directionalLight.color, 1.0);
	
	return ((diffuse * material.diffuseStrenght) + (specular * material.specularStrenght)) * directionalLight.intensity;
}

void main() {

	vec4 color;
	if (material.hasTexture == 1) {
		color = texture(texture0, vec2(exTextCoords.x / (1.0 / nbComposedMeshWidth), exTextCoords.y / (1.0 / nbComposedMeshHeight)));
	} else {
		color = vec4(1.0, 1.0, 1.0, 1.0);
	}
	  
	// Ambient
	vec4 ambient = vec4(material.ambient, 1.0) * material.ambientStrength;
	
	// Diffuse and specular for point lights
	vec4 pointLightColor;
	for(int i = 0; i < MAX_POINT_LIGHT; i++) {
		if(pointLight[i].color != vec3(0.0, 0.0, 0.0)){
			pointLightColor += calculPointLightColor(i);
		}
	}
	
	// Diffuse and specular for directional light
	vec4 directionalLightColor = calculDirectionalLightColor();
	
// 	fragColor = (ambient + pointLightColor + directionalLightColor) * color;

 	fragColor = max(ambient, pointLightColor + directionalLightColor) * color;
}
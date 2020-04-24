#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textCoords;
layout(location = 2) in vec3 color;
layout(location = 3) in vec3 normal;

out vec2 exTextCoords;
out vec3 exColor;
out vec3 exNormal;
out vec3 fragPos;

uniform mat4 projection;
uniform mat4 meshTransform;
uniform mat4 cameraTransform;
uniform float textureRotation;

void main() {
	gl_Position = projection * cameraTransform * meshTransform * vec4(position, 1.0);
	//
	vec2 coord = textCoords;
    float sin_factor = sin(textureRotation);
    float cos_factor = cos(textureRotation);
    coord = (coord - 0.5) * mat2(cos_factor, sin_factor, -sin_factor, cos_factor);
    coord += 0.5;
	exTextCoords = coord;
	//
	exColor = color;
	exNormal = vec3(meshTransform * vec4(normal, 0.0)).xyz;
	fragPos = vec3(meshTransform * vec4(position, 1.0));
}
#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textCoord;

out vec2 exTextCoord;

uniform mat4 projection;
uniform mat4 meshTransform;
uniform mat4 camera;

void main(){
	gl_Position = projection * camera * meshTransform * vec4(position, 1.0);
	exTextCoord = textCoord;
}
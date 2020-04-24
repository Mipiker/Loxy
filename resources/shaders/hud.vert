#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textCoord;

out vec2 exTextCoord;

uniform mat4 ortho;
uniform mat4 meshTransform;

void main(){
	exTextCoord = textCoord;
	gl_Position = ortho * meshTransform * vec4(position, 1.0);
}
#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textCoord;

out vec2 exTextCoord;
out vec2 exPosition;

uniform mat4 ortho;
uniform mat4 meshTransform;
uniform vec2 size;

void main(){
	exTextCoord = textCoord;
	gl_Position = ortho * meshTransform * vec4(position, 1.0);;
	exPosition = size * position.xy;
}
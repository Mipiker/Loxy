#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textCoord;

out vec2 blurTextCoord[11];

uniform float blurHeight;

void main(){
	gl_Position = vec4(position.xy, 0.5, 1.0);
	for(int i = -5; i <= 5; i++) {
		blurTextCoord[i + 5] = textCoord + vec2(0.0, (1.0 / blurHeight) * i);
	}
}
#version 330

in vec2 exTextCoord;

out vec4 fragColor;

uniform sampler2D texture0;
uniform vec3 ambientLight;

void main() {
	fragColor = texture(texture0, exTextCoord) * vec4(ambientLight, 1.0);
}
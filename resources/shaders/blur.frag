#version 330

in vec2 exTextCoord;
in vec2 blurTextCoord[11];

out vec4 fragColor;

uniform sampler2D texture0;

void main() {
	fragColor = vec4(0.0);
	fragColor += texture(texture0, blurTextCoord[0]) * 0.0093;
	fragColor += texture(texture0, blurTextCoord[1]) * 0.028002;
	fragColor += texture(texture0, blurTextCoord[2]) * 0.065984;
	fragColor += texture(texture0, blurTextCoord[3]) * 0.121703;
	fragColor += texture(texture0, blurTextCoord[4]) * 0.175713;
	fragColor += texture(texture0, blurTextCoord[5]) * 0.198596;
	fragColor += texture(texture0, blurTextCoord[6]) * 0.175713;
	fragColor += texture(texture0, blurTextCoord[7]) * 0.121703;
	fragColor += texture(texture0, blurTextCoord[8]) * 0.065984;
	fragColor += texture(texture0, blurTextCoord[9]) * 0.028002;
	fragColor += texture(texture0, blurTextCoord[10]) * 0.0093;
}
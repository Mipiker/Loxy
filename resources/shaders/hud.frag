#version 330

in vec2 exTextCoord;

out vec4 fragColor;

uniform sampler2D texture0;
uniform vec4 color;
uniform int background;
uniform int hasTexture;

void main() {
	fragColor = color;
	if(hasTexture == 1){
		fragColor = fragColor * texture(texture0, exTextCoord);
	}
	if(background == 1){
		fragColor = vec4(fragColor.x, fragColor.y, fragColor.z, max(0.5, fragColor.w));
	}
}
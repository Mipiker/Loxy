#version 330

in vec2 exTextCoord;
in vec2 exPosition;

out vec4 fragColor;

uniform sampler2D texture0;
uniform vec4 color;
uniform int background;
uniform int hasTexture;
uniform int frame;
uniform vec2 size;
uniform vec4 frameColor;

void main() {
	fragColor = color;
	if(hasTexture == 1){
		fragColor = fragColor * texture(texture0, exTextCoord);
	}
	if(background == 1){
		fragColor = vec4(fragColor.x, fragColor.y, fragColor.z, max(0.5, fragColor.w));
	}
	if(frame == 1) {
		if(size.x - exPosition.x < 1
			|| exPosition.x < 1
			|| size.y - exPosition.y < 1
			|| exPosition.y < 1) {
			fragColor = frameColor;	
		}
	}
}
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
uniform float roundCornerSize;

void main() {
	fragColor = color;
	if(hasTexture == 1) {
		fragColor = fragColor * texture(texture0, exTextCoord);
	}
	if(background == 1) {
		fragColor = vec4(fragColor.x, fragColor.y, fragColor.z, max(0.5, fragColor.w));
	}
	if(frame == 1) {
		if(size.x - exPosition.x < roundCornerSize && size.y - exPosition.y < roundCornerSize) { // Top right
			vec2 d = (size - vec2(roundCornerSize, roundCornerSize)) - exPosition;
			if(d.x * d.x + d.y * d.y > roundCornerSize * roundCornerSize) {
				fragColor = vec4(0.0, 0.0, 0.0, 0.0);
			}
		} else if(exPosition.x < roundCornerSize && exPosition.y < roundCornerSize) { // Bottom left
			vec2 d = vec2(roundCornerSize, roundCornerSize) - exPosition;
			if(d.x * d.x + d.y * d.y > roundCornerSize * roundCornerSize) {
				fragColor = vec4(0.0, 0.0, 0.0, 0.0);
			}
		} else if(size.x - exPosition.x < roundCornerSize && exPosition.y < roundCornerSize){ // Top right
			vec2 d = vec2(size.x - roundCornerSize - exPosition.x, roundCornerSize - exPosition.y);
			if(d.x * d.x + d.y * d.y > roundCornerSize * roundCornerSize) {
				fragColor = vec4(0.0, 0.0, 0.0, 0.0);
			}
		} else if( exPosition.x < roundCornerSize && size.y - exPosition.y < roundCornerSize ) { // Top left
			vec2 d = vec2(roundCornerSize - exPosition.x, size.y - roundCornerSize - exPosition.y);
			if(d.x * d.x + d.y * d.y > roundCornerSize * roundCornerSize) {
				fragColor = vec4(0.0, 0.0, 0.0, 0.0);
			}
		}
	}
}
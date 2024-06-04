#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 mask = texture(Sampler1, gl_FragCoord.xy / ScreenSize);
    if (mask.a == 0) {
        discard;
    }
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    fragColor = color * ColorModulator;
}

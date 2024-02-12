#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;

in vec4 texProj0;

const vec3[] COLORS = vec3[](
    vec3(0.0110435, 0.0491995, 0.110818),
    vec3(0.005946, 0.047962, 0.089485),
    vec3(0.013818, 0.0508445, 0.100326),
    vec3(0.023282, 0.0549415, 0.114838),
    vec3(0.0324505, 0.058848, 0.097189),
    vec3(0.0318805, 0.0434475, 0.123646),
    vec3(0.0424085, 0.055997, 0.166380),
    vec3(0.0487445, 0.077060, 0.091064),
    vec3(0.053076, 0.065572, 0.195191),
    vec3(0.0488605, 0.055093, 0.187229),
    vec3(0.066758, 0.069139, 0.148582),
    vec3(0.035003, 0.121666, 0.235792),
    vec3(0.098383, 0.0714495, 0.214696),
    vec3(0.0236405, 0.157669, 0.321970),
    vec3(0.1023375, 0.195005, 0.302066),
    vec3(0.0404775, 0.1574105, 0.661491)
);

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 17.0 / layer,
        0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2((4.5 - layer / 4.0) * 2.0);

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * COLORS[0];
    for (int i = 0; i < EndPortalLayers; i++) {
        color += textureProj(Sampler1, texProj0 * end_portal_layer(float(i + 1))).rgb * COLORS[i];
    }
    fragColor = vec4(color, 1.0);
}

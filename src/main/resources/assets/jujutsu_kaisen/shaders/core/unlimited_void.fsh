#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float LayerOffset;

in vec3 view;
in vec3 uv3d;

vec2 angle2vec2(float radians) {
    return vec2(cos(radians), sin(radians));
}

vec2 portal_layer_uv(float rotation, float view_depth) {
    vec2 dir_vec = angle2vec2(radians(rotation));
    mat2 rotate = mat2(
        dir_vec.x, -dir_vec.y,
        dir_vec.y, dir_vec.x
    );
    return (uv3d.st - (view / view.y).xz * view_depth) * rotate;
}

out vec4 fragColor;

const vec4[] COLORS = vec4[](
    vec4(0.0110435, 0.0491995, 0.110818, 1.0),
    vec4(0.005946, 0.047962, 0.089485, 1.0),
    vec4(0.013818, 0.0508445, 0.100326, 1.0),
    vec4(0.023282, 0.0549415, 0.114838, 1.0),
    vec4(0.0324505, 0.058848, 0.097189, 1.0),
    vec4(0.0318805, 0.0434475, 0.123646, 1.0),
    vec4(0.0424085, 0.055997, 0.166380, 1.0),
    vec4(0.0487445, 0.077060, 0.091064, 1.0),
    vec4(0.053076, 0.065572, 0.195191, 1.0),
    vec4(0.0488605, 0.055093, 0.187229, 1.0),
    vec4(0.066758, 0.069139, 0.148582, 1.0),
    vec4(0.035003, 0.121666, 0.235792, 1.0),
    vec4(0.098383, 0.0714495, 0.214696, 1.0),
    vec4(0.0236405, 0.157669, 0.321970, 1.0),
    vec4(0.1023375, 0.195005, 0.302066, 1.0),
    vec4(0.0404775, 0.1574105, 0.661491, 1.0)
);

void main() {
    vec4 color = texture2D(Sampler0, portal_layer_uv(0.0, uv3d.p - (65.0 * sign(view.y))) * 0.125) * COLORS[0];

    vec2 uv_offset = vec2(0.0, LayerOffset);

    for (int i = 1; i < 16; i++) {
        for (int layer_sign = -1; layer_sign <= 1; layer_sign += 2) {
            float layer_num = float(i * layer_sign) * -sign(view.y);
            float layer_rotation = (layer_num * layer_num * 4321.0 + layer_num * 9.0) * 2.0;
            float layer_scale = i == 1 ? 0.5 : 0.0625;
            float layer_depth = (15.5 - float(i)) * float(layer_sign) * -sign(view.y);
            float layer_view_depth = uv3d.p + layer_depth;

            if (sign(layer_view_depth) != sign(view.y)) {
                color += texture2D(Sampler1, portal_layer_uv(layer_rotation, layer_view_depth) * layer_scale + uv_offset) * COLORS[i];
            }
        }
    }
    fragColor = vec4(color.rgb, 1.0);
}
#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D SlicedEntityDepthSampler;

uniform mat4 InvModelViewMat;
uniform mat4 InvProjMat;
uniform vec3 Transform;
uniform vec4 Plane;
uniform float Direction;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = texture(SlicedEntityDepthSampler, texCoord).r;
    vec4 clip = vec4(texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 cam = InvProjMat * clip;
    cam /= cam.w;
    vec4 world = InvModelViewMat * cam;
    float distance = dot(world.xyz - Transform, Plane.xyz) - Plane.w;
    if ((Direction == 1 && distance < 0.0) || (Direction == -1 && distance > 0.0)) {
        discard;
    }
    fragColor = texture(DiffuseSampler, texCoord);
}
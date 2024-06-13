#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D SlicedEntityDepthSampler;

uniform mat4 RealModelViewMat;
uniform mat4 RealProjMat;
uniform float NearPlane;
uniform float FarPlane;
uniform vec3 Pos;
uniform vec4 Plane;
uniform float Direction;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = texture(SlicedEntityDepthSampler, texCoord).r;
    vec4 clip = vec4(texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 cam = inverse(RealProjMat) * clip;
    cam /= cam.w;
    vec4 world = inverse(RealModelViewMat) * cam;
    float distance = dot(world.xyz, Plane.xyz) - Plane.w;
    if ((Direction == 1 && distance < 0.0) || (Direction == -1 && distance > 0.0)) {
        discard;
    }
    fragColor = texture(DiffuseSampler, texCoord);
}
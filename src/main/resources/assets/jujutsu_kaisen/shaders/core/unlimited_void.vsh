#version 150

in vec3 Position;
in vec2 UV;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform vec3 CameraPos;

out vec3 view;
out vec3 uv3d;

vec4 ModelPos = vec4(Position, 1.0);
mat4 ICamJiggleMat = mat4(inverse(mat3(ProjMat))) * ProjMat;

void main() {
    ICamJiggleMat[2].w = 0.0;
    ICamJiggleMat[3].z = 0.0;

    gl_Position = ProjMat * ModelPos;
    view = IViewRotMat * (ICamJiggleMat * ModelPos).xyz;
    uv3d.st = (IViewRotMat * Position).xz;
    uv3d.st += (ModelViewMat * vec4(CameraPos, 1.0)).xz;
    uv3d.z = UV.y;
}
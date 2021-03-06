#version 460 core

layout (location = 0) in vec3 vertex;

uniform mat4 pvm;

void main() {
    gl_Position = pvm * vec4(vertex, 1);
}

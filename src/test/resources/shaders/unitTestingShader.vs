#version 140

in vec2 position;
in vec2 textureCoords;

out vec4 color;
out vec2 uvCoords;

uniform float zIndex;
uniform vec4 matColor;
uniform vec4 offset;
uniform vec3 dummyVar;
uniform vec2 pixelScale;
uniform vec2 screenPosition;

void main()
{
    color = matColor;
    gl_Position = vec4((position * pixelScale) + screenPosition, 0, 1);
    gl_Position.z = zIndex;
    uvCoords = (textureCoords * offset.zw) + offset.xy + dummyVar.xy;
}
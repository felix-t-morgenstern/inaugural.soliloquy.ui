#version 140

in vec2 position;
in vec2 textureCoords;

out vec4 color;
out vec2 uvCoords;

uniform vec4 matColor;
uniform vec4 offset;
uniform vec2 dimensionsInWindow;
uniform vec2 windowPosition;

void main()
{
    color = matColor;
    gl_Position = vec4((position * (dimensionsInWindow * 2)) +
        vec2(
            (windowPosition.x * 2.0) - 1.0,
            (((-windowPosition.y + (1.0 - dimensionsInWindow.y)) * 2.0) - 1.0)
        ),
        0, 1);
    uvCoords = (textureCoords * offset.zw) + offset.xy;
}
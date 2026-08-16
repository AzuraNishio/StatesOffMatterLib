#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float Time;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, floor(16.0 * texCoord0 + (Time * 0.8)) * 0.0625) * 0.7;
    color += texture(Sampler0, floor(16.0 * texCoord0 + (Time * -1.2)) * 0.0625) * 0.3;
    //color.gb *= 0.2;
    color.r += 0.1;
    color.a *= vertexColor.a * (1.0 + vertexColor.r) * 0.8;
    float fade = 1.35 - length(texCoord0 - 0.5);
    color.a *= fade * fade;


    color.rgb *= vertexColor.r * vertexColor.r * 1.6;

    fragColor = color * ColorModulator;
}

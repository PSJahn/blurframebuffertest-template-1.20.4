#version 330

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

#define pow2(x) (x * x)

const float pi = atan(1.0) * 4.0;
const int samples = 20;
const float sigma = float(samples) * 0.25;

float gaussian(vec2 i) {
    return 1.0 / (2.0 * pi * pow2(sigma)) * exp(-((pow2(i.x) + pow2(i.y)) / (2.0 * pow2(sigma))));
}

vec4 blur(sampler2D sp, vec2 uv, vec2 scale) {
    vec4 col = vec4(0.0);
    float accum = 0.0;
    float weight;
    vec2 offset;
    
    for (int x = -samples / 2; x < samples / 2; ++x) {
        for (int y = -samples / 2; y < samples / 2; ++y) {
            offset = vec2(x, y);
            weight = gaussian(offset);
            col += texture(sp, uv + scale * offset) * weight;
            accum += weight;
        }
    }
    
    return col / accum;
}

void main() {
	fragColor = blur(DiffuseSampler, texCoord, oneTexel);
}

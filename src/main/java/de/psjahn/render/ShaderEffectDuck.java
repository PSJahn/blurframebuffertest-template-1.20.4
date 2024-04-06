package de.psjahn.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;

import java.util.List;

public interface ShaderEffectDuck {
    void renderer$addFakeTarget(String name, Framebuffer buffer);

    List<PostEffectPass> renderer$getPasses();
}

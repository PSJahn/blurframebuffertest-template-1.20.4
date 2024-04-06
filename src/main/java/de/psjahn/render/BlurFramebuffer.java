package de.psjahn.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

public class BlurFramebuffer extends Framebuffer {
    private static final ManagedShaderEffect BLUR_SHADER =ShaderEffectManager.getInstance().manage(new Identifier("blurframebuffertest", "shaders/post/blur.json"));
    private static BlurFramebuffer instance;

    private BlurFramebuffer(int width, int height) {
        super(false);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    private static BlurFramebuffer obtain() {
        if (instance == null) {
            instance = new BlurFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight);
        }
        return instance;
    }

    /**
     * Draws to this framebuffer. See javadoc of this class for more information.
     *
     * @param r The action with rendering calls to write to this framebuffer
     */
    public static void use(Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        BlurFramebuffer buffer = obtain();
        if (buffer.textureWidth != mainBuffer.textureWidth || buffer.textureHeight != mainBuffer.textureHeight) {
            buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, false);
        }

        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);

        buffer.beginWrite(true);
        r.run();
        buffer.endWrite();

        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

        mainBuffer.beginWrite(false);
    }

    @Override
    public void resize(int width, int height, boolean getError) {
        if (this.textureWidth != width || this.textureHeight != height) {
            super.resize(width, height, getError);
        }
    }

    public static void draw() {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        BlurFramebuffer buffer = obtain();

        ((ShaderEffectDuck) Objects.requireNonNull(
                BLUR_SHADER.getShaderEffect())).renderer$addFakeTarget("inp", buffer);
        // final buffer is written to here, including transparency
        Framebuffer out = BLUR_SHADER.getShaderEffect().getSecondaryTarget("out");

        BLUR_SHADER.render(MinecraftClient.getInstance().getTickDelta());

        buffer.clear(false);

        mainBuffer.beginWrite(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO,
                DstFactor.ONE);
        RenderSystem.backupProjectionMatrix();
        out.draw(out.textureWidth, out.textureHeight, false);
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public static void useAndDraw(Runnable r) {
        use(r);
        draw();
    }
}
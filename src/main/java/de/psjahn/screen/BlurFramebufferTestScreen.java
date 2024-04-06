package de.psjahn.screen;

import de.psjahn.render.BlurFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BlurFramebufferTestScreen extends Screen {
    public BlurFramebufferTestScreen() { super(NarratorManager.EMPTY); }

    @Override public boolean shouldPause() { return false; }

    boolean blur = false;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(blur) BlurFramebuffer.useAndDraw(()->renderRectangles(context));
        else renderRectangles(context);
    }

    public void renderRectangles(DrawContext context)
    {
        int sw = context.getScaledWindowWidth();
        int sh = context.getScaledWindowHeight();
        context.fill(20, sh-20, sw-20, (sh/2)-20, Color.BLACK.getRGB());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode== GLFW.GLFW_KEY_B) blur=!blur;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

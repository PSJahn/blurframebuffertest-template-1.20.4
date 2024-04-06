package de.psjahn;

import de.psjahn.screen.BlurFramebufferTestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BlurFramebufferTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyBinding toggleGameVisible = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.examplemod.spook",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"category.examplemod.test"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleGameVisible.wasPressed()) {
				//Toggle Game Visibility
				MinecraftClient.getInstance().setScreen(new BlurFramebufferTestScreen());
			}
		});
	}
}
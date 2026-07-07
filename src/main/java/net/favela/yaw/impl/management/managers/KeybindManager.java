package net.favela.yaw.impl.management.managers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.modules.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeybindManager {

    private final Set<Integer> heldKeys = new HashSet<>();

    public void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft client) {
        if (client == null) return;

        long handle = client.getWindow().handle();
        boolean noScreen = client.gui.screen() == null;

        for (Module module : Manager.MODULE.getModules()) {
            int key = module.bind.getKey();
            if (key <= 0) continue;

            boolean down = GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
            boolean firstPress = down && !heldKeys.contains(key);

            if (firstPress && noScreen) {
                module.toggle();
            }
        }

        updateHeldKeys(handle);
    }

    private void updateHeldKeys(long handle) {
        for (Module module : Manager.MODULE.getModules()) {
            int key = module.bind.getKey();
            if (key <= 0) continue;

            boolean down = GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
            if (down) heldKeys.add(key);
            else heldKeys.remove(key);
        }
    }
}
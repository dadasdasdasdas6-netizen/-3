package net.favela.yaw.impl.event.events;

import net.favela.yaw.impl.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;

public record Render3DEvent(PoseStack poseStack, float partialTicks) implements Event {
    public PoseStack getMatrix() {
        return poseStack;
    }

    public float getDelta() {
        return partialTicks;
    }
}
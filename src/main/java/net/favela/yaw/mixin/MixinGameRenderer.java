package net.favela.yaw.mixin;

import net.favela.yaw.impl.event.Events;
import net.favela.yaw.impl.event.events.Render2DEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Final
    @Shadow
    private GameRenderState gameRenderState;

    @Inject(method = "extract", at = @At("TAIL"))
    private void yaw$onExtract(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        int mouseX = (int) mc.mouseHandler.xpos();
        int mouseY = (int) mc.mouseHandler.ypos();
        GuiGraphicsExtractor context = new GuiGraphicsExtractor(
                mc,
                gameRenderState.guiRenderState,
                mouseX,
                mouseY
        );
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        Events.post(new Render2DEvent(context, delta));
    }
}
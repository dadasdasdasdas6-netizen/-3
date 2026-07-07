package net.favela.yaw.mixin;

import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.categories.render.Crosshair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.Hud.class)
public class MixinHud {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void favelayaw$renderHud(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
        Hud.renderAll(context);
    }

    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void favelayaw$cancelCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Crosshair.INSTANCE != null && Crosshair.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}
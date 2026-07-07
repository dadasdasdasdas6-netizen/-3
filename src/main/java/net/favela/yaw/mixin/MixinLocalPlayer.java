package net.favela.yaw.mixin;

import net.favela.yaw.impl.event.Events;
import net.favela.yaw.impl.event.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void yaw$onUpdate(CallbackInfo ci) {
        if (Minecraft.getInstance().player == (Object) this) {
            Events.post(new UpdateEvent());
        }
    }
}
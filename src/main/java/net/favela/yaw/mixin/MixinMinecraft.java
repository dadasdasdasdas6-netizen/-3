package net.favela.yaw.mixin;

import net.favela.yaw.impl.event.Events;
import net.favela.yaw.impl.event.events.TickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "tick", at = @At("HEAD"))
    private void yaw$onTick(CallbackInfo ci) {
        Events.post(new TickEvent());
    }
}
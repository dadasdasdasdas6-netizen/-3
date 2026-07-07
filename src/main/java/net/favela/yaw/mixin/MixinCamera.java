package net.favela.yaw.mixin;

import net.favela.yaw.impl.modules.categories.render.ViewClip;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @ModifyArgs(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private void yaw$onAlignWithEntity(Args args) {
        ViewClip viewClip = ViewClip.INSTANCE;
        if (viewClip != null && viewClip.isEnabled() && viewClip.player.get()) {
            args.set(0, viewClip.range.getFloat());
        }
    }

    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void yaw$onGetMaxZoom(float cameraDist, CallbackInfoReturnable<Float> cir) {
        ViewClip viewClip = ViewClip.INSTANCE;
        if (viewClip != null && viewClip.isEnabled()) {
            cir.setReturnValue(cameraDist);
        }
    }
}
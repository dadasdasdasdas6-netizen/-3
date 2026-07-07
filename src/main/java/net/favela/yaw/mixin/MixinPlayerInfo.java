package net.favela.yaw.mixin;

import net.favela.yaw.impl.modules.categories.client.Capes;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo {

    @Inject(method = "getSkin", at = @At("TAIL"), cancellable = true)
    private void onGetSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        Capes capes = Capes.getInstance();
        if (capes == null || !capes.isEnabled()) return;

        PlayerInfo entry = (PlayerInfo)(Object)this;
        UUID uuid = entry.getProfile().id();

        if (MC.player == null || !uuid.equals(MC.player.getGameProfile().id())) return;

        ClientAsset.ResourceTexture capeTexture = capes.getCapeTexture();
        PlayerSkin original = cir.getReturnValue();

        cir.setReturnValue(PlayerSkin.insecure(
                original.body(),
                capeTexture,
                capeTexture,
                original.model()
        ));
    }
}
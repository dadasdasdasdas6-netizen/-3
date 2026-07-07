package net.favela.yaw.mixin;

import net.favela.yaw.impl.modules.categories.misc.DeathScreen;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.DeathScreen.class)
public abstract class MixinDeathScreen extends Screen {

    @Shadow protected abstract void setButtonsActive(boolean bl);

    protected MixinDeathScreen() {
        super(Component.literal("q"));
    }

    @Redirect(method = "visitText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/network/chat/Component;)V", ordinal = 1))
    private void favelayaw$redirectDeathText(ActiveTextCollector collector, TextAlignment alignment, int x, int y, Component text) {
        DeathScreen module = DeathScreen.INSTANCE;
        if (module != null && module.isEnabled() && module.customText.get()) {
            text = Component.literal(module.text.get());
        }
        collector.accept(alignment, x, y, text);
    }

    @Redirect(method = "visitText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/network/chat/Component;)V", ordinal = 2))
    private void favelayaw$redirectScoreText(ActiveTextCollector collector, TextAlignment alignment, int x, int y, Component text) {
        DeathScreen module = DeathScreen.INSTANCE;
        if (module != null && module.isEnabled() && module.customScore.get() && this.minecraft != null && this.minecraft.player != null) {
            text = Component.literal(module.score.get() + "; " + this.minecraft.player.getScore());
        }
        collector.accept(alignment, x, y, text);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void favelayaw$tick(CallbackInfo ci) {
        DeathScreen module = DeathScreen.INSTANCE;
        if (module != null && module.isEnabled() && module.noDelay.get()) {
            ci.cancel();
            super.tick();
            this.setButtonsActive(true);
        }
    }
}
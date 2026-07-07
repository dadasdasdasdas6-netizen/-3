package net.favela.yaw.mixin;

import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.modules.categories.client.Streamer;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@Mixin(StringDecomposer.class)
public class MixinStringDecomposer {
    @Unique
    private static String CACHED_NAME;

    @ModifyVariable(
            method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            name = "string")

    private static String replaceText(String string) {
        if (Manager.MODULE == null || string == null || string.isEmpty()) return string;
        Streamer streamer = Manager.MODULE.get(Streamer.class);
        if (streamer == null || !streamer.isEnabled()) return string;
        if (CACHED_NAME == null && MC != null) {
            CACHED_NAME = MC.getUser().getName();
        }

        if (CACHED_NAME == null || CACHED_NAME.isEmpty()) return string;
        return string.replace(CACHED_NAME, streamer.name.get());
    }
}
package net.favela.yaw.mixin;

import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.modules.categories.client.Streamer;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@Mixin(StringDecomposer.class)
public class MixinStringDecomposer {

    @ModifyVariable(
            method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            name = "string")
    private static String yaw$replaceText(String string) {
        if (Manager.MODULE == null || string == null || string.isEmpty()) return string;

        Streamer streamer = Manager.MODULE.get(Streamer.class);
        if (streamer == null || !streamer.isEnabled()) return string;

        String real = yaw$realName();
        if (real == null || real.isEmpty() || string.indexOf(real) < 0) return string;

        String fake = streamer.name.get();
        if (fake == null || fake.isEmpty() || fake.equals(real)) return string;

        return string.replace(real, fake);
    }

    @Unique
    private static String yaw$realName() {
        if (MC == null || MC.getUser() == null) return null;
        return MC.getUser().getName();
    }
}
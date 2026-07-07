package net.favela.yaw.mixin;

import net.favela.yaw.EntryPoint;
import net.favela.yaw.impl.modules.categories.client.Management;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void yaw$renderBranding(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        Management mgmt = Management.getInstance();

        if (mgmt == null || !mgmt.isEnabled()) return;

        Font font = Minecraft.getInstance().font;
        String name = EntryPoint.name();
        String version = "v" + EntryPoint.version();

        int themeColor = mgmt.nameColor.get().getRGB();
        int white = mgmt.versionColor.get().getRGB();

        int x = 2;
        int y = 2;

        graphics.text(font, name, x, y, themeColor);
        x += font.width(name) + 3;
        graphics.text(font, version, x, y, white);
    }
}
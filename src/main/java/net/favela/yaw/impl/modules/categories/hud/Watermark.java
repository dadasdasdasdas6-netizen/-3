package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.EntryPoint;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Watermark extends Hud {
    private static final String GIT_HASH = ""; // Define the git hash constant

    public Watermark() {
        super("Watermark", "Client watermark", 40, 9);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        Font font = MC.font;
        String name = EntryPoint.name();
        String version = " " + EntryPoint.version();
        String hash = " " + GIT_HASH;

        int x = (int) getX();
        int y = (int) getY();

        HUD editor = HUD.getInstance();
        int nameColor = editor != null ? editor.getColor(0).getRGB() : (GUI.INSTANCE != null ? GUI.INSTANCE.theme.getRGB() : new Color(255, 255, 255).getRGB());

        context.text(font, name, x, y, nameColor, true);
        context.text(font, version, x + font.width(name), y, 0xFFFFFFFF, true);
        context.text(font, hash, x + font.width(name + version), y, 0xFF888888, true);

        setWidth(font.width(name + version + hash));
        setHeight(font.lineHeight);
    }
}

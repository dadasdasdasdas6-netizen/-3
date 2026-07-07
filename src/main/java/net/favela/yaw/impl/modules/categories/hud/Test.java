package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Test extends Hud {

    public Test() {
        super("Test", "Displays your name and fall distance", 100, 100);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        if (MC.player == null || MC.level == null) return;

        Font font = MC.font;

        String name = MC.player.getGameProfile().name();
        double dist = MC.player.fallDistance;
        String text = name + (dist != 0.0 ? " " + dist : "");

        int color = GUI.INSTANCE != null ? GUI.INSTANCE.theme.getRGB() : 0xFFA387FF;

        context.text(font, text, (int) getX(), (int) getY(), color, true);

        setWidth(font.width(text));
        setHeight(font.lineHeight + 2);
    }
}
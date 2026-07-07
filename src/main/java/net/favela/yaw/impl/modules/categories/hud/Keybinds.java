package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.ColorSetting;
import net.favela.yaw.impl.util.wrapper.Wrapper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

@AutoService(Module.class)
public class Keybinds extends Hud implements Wrapper {

    public Keybinds() {
        super("Keybinds", "Displays bound modules", 88, 14);
    }

    public ColorSetting cs = color("Accent", new Color(255, 255, 255));

    @Override
    public void render(GuiGraphicsExtractor context) {
        int currY = 10;
        int maxWidth = 88;
        int totalHeight = 20;
        boolean hasModules = false;

        for (Module m : Manager.MODULE.getModules()) {
            if (m.bind.getKey() != -1 && m.isEnabled()) {
                totalHeight += MC.font.lineHeight;

                int requiredWidth = MC.font.width(m.getName()) + MC.font.width("[toggled]") + 8;
                if (requiredWidth > maxWidth) {
                    maxWidth = requiredWidth;
                }
                hasModules = true;
            }
        }

        if (!hasModules) {
            return;
        }

        setWidth(maxWidth);
        setHeight(totalHeight);

        context.fill((int) getX(), (int) getY(), (int) (getX() + getWidth()), (int) (getY() + 2), cs.getRGB());
        context.fill((int) getX(), (int) getY(), (int) (getX() + getWidth()), (int) (getY() + totalHeight + 2), new Color(0, 0, 0, 100).getRGB());
        context.text(MC.font, "keybinds", (int) (getX() + (getWidth() - MC.font.width("keybinds")) / 2), (int) (getY() + 5), Color.WHITE.getRGB());

        for (Module m : Manager.MODULE.getModules()) {
            if (m.bind.getKey() != -1 && m.isEnabled()) {
                currY += MC.font.lineHeight;
                context.text(MC.font, m.getName(), (int) getX() + 2, (int) (getY() + currY), Color.WHITE.getRGB());
                context.text(MC.font, "[toggled]", (int) (getX() + getWidth() - MC.font.width("[toggled]") - 2), (int) (getY() + currY), Color.WHITE.getRGB());
            }
        }
    }
}
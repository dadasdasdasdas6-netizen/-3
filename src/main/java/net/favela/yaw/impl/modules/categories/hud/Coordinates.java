package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Coordinates extends Hud {

    public final BooleanSetting cameraPos = bool("Camera Pos", false);

    public Coordinates() {
        super("Coordinates", "Displays player coordinates with nether conversion", 100, 9);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        if (MC.level == null || MC.player == null) return;

        Font font = MC.font;
        boolean bl = cameraPos.get();
        Vec3 pos = bl ? MC.gameRenderer.mainCamera().position() : MC.player.position();
        double xP = pos.x;
        double yP = pos.y;
        double zP = pos.z;

        boolean isNether = MC.level.dimension() == Level.NETHER;
        double xPA = isNether ? xP * 8.0 : xP / 8.0;
        double zPA = isNether ? zP * 8.0 : zP / 8.0;

        int x = (int) getX();
        int y = (int) getY();

        String prefix = "XYZ: ";
        String main = String.format("%.1f, %.1f, %.1f", xP, yP, zP);
        String nether = String.format(" [%.1f, %.1f, %.1f]", xPA, yP, zPA);

        int mainColor = bl ? new Color(180, 250, 250, 255).getRGB() : -1;
        int netherColor = new Color(170, 170, 170, 255).getRGB();

        float xOffset = x;
        for (int i = 0; i < prefix.length(); ++i) {
            String ch = String.valueOf(prefix.charAt(i));
            Color colorForChar = HUD.getInstance().getColor(i);
            context.text(font, ch, (int) xOffset, y, colorForChar.getRGB(), true);
            xOffset += font.width(ch);
        }
        context.text(font, main, (int) xOffset, y, mainColor, true);
        xOffset += font.width(main);
        context.text(font, nether, (int) xOffset, y, netherColor, true);

        setWidth(font.width(prefix + main + nether));
        setHeight(font.lineHeight);
    }
}
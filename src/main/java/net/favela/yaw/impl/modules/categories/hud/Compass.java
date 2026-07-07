package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.setting.settings.EnumSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Compass extends Hud {

    private static final String[] DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private static final String[] DIRECTIONS_ALT = {"Z-", "X+Z-", "X+", "X+Z+", "Z+", "X-Z+", "X-", "X-Z-"};

    public final EnumSetting<MODE> mode = enm("Mode", MODE.WORLD);

    public Compass() {
        super("Compass", "Rust compass", 150, 9);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        if (MC.level == null || MC.player == null) return;

        Font font = MC.font;

        int x = (int) getX();
        int y = (int) getY();
        setWidth(150);
        setHeight(font.lineHeight);

        context.enableScissor(x, y, x + (int) getWidth(), y + (int) getHeight());

        double yaw = (MC.player.getYRot() + 180.0f) % 360.0f;
        if (yaw < 0.0) {
            yaw += 360.0;
        }

        double segmentPosition = yaw / 15.0;
        double fractional = segmentPosition - Math.floor(segmentPosition);
        int currentSegment = (int) Math.floor(segmentPosition);
        int centerX = x + (int) getWidth() / 2;

        int accent = GUI.INSTANCE != null ? GUI.INSTANCE.theme.getRGB() : 0xFFA387FF;

        for (int i = -10; i <= 10; ++i) {
            int segmentIndex = (currentSegment + i + 48) % 24;
            String text = segmentIndex % 3 == 0 ? (mode.get() == MODE.WORLD ? DIRECTIONS[segmentIndex / 3] : DIRECTIONS_ALT[segmentIndex / 3]) : String.valueOf(segmentIndex * 15);

            double positionOffset = (double) i - fractional;
            double xPos = (double) centerX + positionOffset * 30.0;
            int textWidth = font.width(text);
            int textX = (int) (xPos - (double) textWidth / 2.0);

            double distance = Math.abs(positionOffset);
            int alpha = (int) (255.0 * Math.max(0.0, 1.0 - distance * 0.3));
            alpha = Math.max(0, Math.min(255, alpha));

            boolean isCurrent = Math.abs(positionOffset) < 0.5;
            int base = isCurrent ? accent : 0xAAAAAA;
            int color = (base & 0xFFFFFF) | (alpha << 24);

            context.text(font, text, textX, y, color, true);
        }

        context.disableScissor();
    }

    public enum MODE {
        WORLD,
        DIRECTION
    }
}
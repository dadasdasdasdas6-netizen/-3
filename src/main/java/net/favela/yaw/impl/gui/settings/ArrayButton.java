package net.favela.yaw.impl.gui.settings;

import net.favela.yaw.impl.setting.settings.SetSetting;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;
import java.util.Set;

public class ArrayButton extends Button {

    private final SetSetting<?> setting;

    public ArrayButton(SetSetting<?> setting, ModuleButton btn) {
        super(setting, btn);
        this.setting = setting;
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int mx, int my, float t, int a) {
        renderHover(ctx, mx, my, a);
        float hover = hoverAnim.get();
        if (hover > 0.001f) renderArray(ctx, mx, my, (int) (a * hover));
        drawString(ctx, setting.getName(), getX() + 2, getY(), new Color(255, 255, 255, a).getRGB(), a);
        int size = setting.size();
        String count = "[" + size + "]";
        drawString(ctx, count, getX() + getWidth() - 2 - width(count), getY(), new Color(200, 200, 200, a).getRGB(), a);
    }

    private void renderArray(GuiGraphicsExtractor ctx, int mx, int my, int a) {
        Set<?> set = setting.get();
        if (set == null || set.isEmpty()) return;
        int maxW = 0;
        for (Object o : set) maxW = Math.max(maxW, width(String.valueOf(o)) + 4);
        int lineH = mc.font.lineHeight + 2;
        int totalH = set.size() * lineH + 4;
        int border = new Color(128, 128, 128, a).getRGB();
        RenderUtil.rect(ctx, mx, my, mx + maxW, my + totalH, new Color(0, 0, 0, (int) (200 * (a / 255f))).getRGB());
        RenderUtil.rect(ctx, mx, my, mx + maxW, my + 1, border);
        RenderUtil.rect(ctx, mx, my + totalH - 1, mx + maxW, my + totalH, border);
        RenderUtil.rect(ctx, mx, my, mx + 1, my + totalH, border);
        RenderUtil.rect(ctx, mx + maxW - 1, my, mx + maxW, my + totalH, border);
        int yOff = my + 2;
        for (Object o : set) {
            drawString(ctx, String.valueOf(o), mx + 2, yOff, new Color(255, 255, 255, a).getRGB(), a);
            yOff += lineH;
        }
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {
    }

    @Override
    public int getTotal() {
        return getHeight();
    }
}
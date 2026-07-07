package net.favela.yaw.impl.gui.settings;

import net.favela.yaw.impl.gui.GUIScreen;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.setting.settings.BindSetting;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public class BindButton extends Button {

    private final BindSetting setting;

    public BindButton(BindSetting setting, ModuleButton btn) {
        super(setting, btn);
        this.setting = setting;
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int mx, int my, float delta, int alpha) {
        super.render(ctx, mx, my, delta, alpha);
        float o = openAnim.to(isOpen() ? 1f : 0f, 25f);
        if (o > 0.001f) {
            Color theme = GUI.INSTANCE.theme.get();
            RenderUtil.rect(ctx, getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    new Color(theme.getRed(), theme.getGreen(), theme.getBlue(),
                            (int) (theme.getAlpha() * 0.6f * o * (alpha / 255f))).getRGB());
        }
        renderHover(ctx, mx, my, alpha);
        if (isOpen()) {
            drawString(ctx, "\u00a77Press button" + GUIScreen.getInstance().getSym(), getX() + 2, getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
        } else {
            String val = formatBind(setting.getKey());
            drawString(ctx, "Button", getX() + 2, getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
            drawString(ctx, "\u00a77" + val, getX() + getWidth() - 2 - width(val), getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
        }
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {
        if (isOpen()) {
            setting.setKey(-(btn + 2));
            setOpen(false);
            return;
        }
        if (isHovering(mx, my) && btn == 0) setOpen(true);
    }

    @Override
    public void onKeyPressed(int key) {
        if (isOpen()) {
            if (key == 259 || key == 256 || key == 261) setting.setKey(-1);
            else setting.setKey(key);
            setOpen(false);
        }
    }

    private String formatBind(int key) {
        if (key == -1) return "NONE";
        if (key < -1) return "MOUSE " + (-key - 1);
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();
        return "KEY " + key;
    }

    @Override
    public int getTotal() {
        return getHeight();
    }
}
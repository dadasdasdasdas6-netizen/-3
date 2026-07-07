package net.favela.yaw.impl.gui.settings;

import net.favela.yaw.impl.gui.GUIScreen;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.favela.yaw.impl.util.animation.Anim;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderButton extends Button {

    private final Number min;
    private final Number max;
    private final NumberSetting setting;
    private boolean drag;
    public String displayText = "";

    private final Anim fillAnim = new Anim(0f);

    public SliderButton(NumberSetting setting, ModuleButton btn) {
        super(setting, btn);
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int mx, int my, float delta, int alpha) {
        super.render(ctx, mx, my, delta, alpha);
        if (drag) drag(mx);
        float fill = setting.get().floatValue() <= min.floatValue() ? 0f : partialMultiplier();
        float animated = fillAnim.to(fill, 25f);
        float fillW = Math.max(1f, getWidth() * animated);
        Color theme = GUI.INSTANCE.theme.get();
        RenderUtil.rect(ctx, getX(), getY(), getX() + fillW, getY() + getHeight(),
                new Color(theme.getRed(), theme.getGreen(), theme.getBlue(),
                        (int) (theme.getAlpha() * (alpha / 255f))).getRGB());
        renderHover(ctx, mx, my, alpha);
        if (isOpen()) {
            drawString(ctx, displayText + GUIScreen.getInstance().getSym(), getX(), getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
        } else {
            drawString(ctx, setting.getName(), getX() + 2, getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
            String value = setting.getRenderText();
            drawString(ctx, "\u00a77" + value, getX() + getWidth() - width(value) - 2, getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);
        }
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {
        if (isHovering(mx, my)) {
            if (btn == 0 && !isOpen()) drag = true;
            if (btn == 1) setOpen(!isOpen());
        }
    }

    @Override
    public void mouseReleased(int mx, int my, int btn) {
        if (btn == 0 && drag) drag = false;
    }

    @Override
    public void onKeyPressed(int key) {
        if (isOpen()) {
            if (key == 259 && !displayText.isEmpty()) {
                displayText = displayText.substring(0, displayText.length() - 1);
            } else if (key == 257) {
                try {
                    if (setting.get() instanceof Float) setting.set(Float.valueOf(displayText));
                    else if (setting.get() instanceof Double) setting.set(Double.valueOf(displayText));
                    else if (setting.get() instanceof Long) setting.set(Long.valueOf(displayText));
                    else if (setting.get() instanceof Integer) setting.set(Integer.valueOf(displayText));
                    displayText = "";
                    setOpen(false);
                } catch (Exception e) {
                    displayText = "";
                    setOpen(false);
                }
            } else if (key == 256) {
                setOpen(false);
            }
        }
    }

    @Override
    public void onCharTyped(char typedChar, int keyCode) {
        if (isOpen()) {
            if (Character.isDigit(typedChar)) {
                displayText = displayText + typedChar;
            } else if (!(displayText.contains(".") || typedChar != '.' && typedChar != ',' || setting.get() instanceof Integer || setting.get() instanceof Long)) {
                displayText = displayText + ".";
            } else if (!displayText.contains("-") && typedChar == '-') {
                displayText = displayText + "-";
            }
        }
    }

    @Override
    public int getTotal() {
        return getHeight();
    }

    @Override
    public boolean isHovering(double x, double y) {
        return x >= getX() && x <= getX() + getWidth() && y >= getY() && y <= getY() + getHeight();
    }

    private void drag(int mouseX) {
        int scale = getStep(setting.getStep().floatValue());
        float percent = (mouseX - getX()) / (float) getWidth();
        if (setting.get() instanceof Integer) {
            int result = (int) Math.max(min.intValue(), Math.min(max.intValue(), min.floatValue() + percent * (max.intValue() - min.intValue())));
            setting.set(result);
        } else if (setting.get() instanceof Float) {
            float result = Math.max(min.floatValue(), Math.min(max.floatValue(), min.floatValue() + percent * (max.floatValue() - min.floatValue())));
            result = new BigDecimal(result).setScale(scale, RoundingMode.HALF_UP).floatValue();
            setting.set(result);
        } else if (setting.get() instanceof Double) {
            double result = Math.max(min.doubleValue(), Math.min(max.doubleValue(), min.doubleValue() + percent * (max.doubleValue() - min.doubleValue())));
            result = new BigDecimal(result).setScale(scale, RoundingMode.HALF_UP).doubleValue();
            setting.set(result);
        }
        if (mouseX < getX() + 1) setting.set(min);
        else if (mouseX > getX() + getWidth() - 1) setting.set(max);
    }

    public static int getStep(float number) {
        String string = Float.toString(number);
        int i = string.indexOf(46);
        if (i == -1) return 0;
        return string.length() - i - 1;
    }

    private float middle() {
        return max.floatValue() - min.floatValue();
    }

    private float part() {
        return setting.get().floatValue() - min.floatValue();
    }

    private float partialMultiplier() {
        return part() / middle();
    }
}
package net.favela.yaw.impl.gui.settings;

import net.favela.yaw.impl.gui.GUIScreen;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.setting.settings.ColorSetting;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;

public class ColorButton extends Button {

    private static final int PAD = 3;
    private static final int HUE_WIDTH = 6;
    private static final int ALPHA_HEIGHT = 6;
    private static final int OUTLINE = 0xFF000000;

    public final ColorSetting setting;
    private float[] selectedColor;
    private boolean dragHue, dragAlpha, dragColor;
    private boolean copyClick, pasteClick, rainbowClick;

    public ColorButton(ColorSetting setting, ModuleButton btn) {
        super(setting, btn);
        this.setting = setting;
        Color v = setting.getRaw();
        selectedColor = Color.RGBtoHSB(v.getRed(), v.getGreen(), v.getBlue(), null);
    }

    private int boxSize() {
        return getWidth() - PAD * 3 - HUE_WIDTH;
    }

    private float boxX() {
        return getX() + PAD;
    }

    private float boxTop() {
        return getY() + getHeight() + PAD;
    }

    private float hueX() {
        return boxX() + boxSize() + PAD;
    }

    private float alphaTop() {
        return boxTop() + boxSize() + PAD;
    }

    private float buttonsTop() {
        float y = boxTop() + boxSize() + PAD;
        if (setting.isAllowAlpha()) y += ALPHA_HEIGHT + PAD;
        return y;
    }

    private float alphaLeft() {
        return getX() + PAD;
    }

    private float alphaRight() {
        return getX() + getWidth() - PAD;
    }

    private int expandedTotal() {
        int total = getHeight() + PAD;
        total += boxSize() + PAD;
        if (setting.isAllowAlpha()) total += ALPHA_HEIGHT + PAD;
        total += getHeight() + PAD; // copy / paste row
        total += getHeight() + PAD; // rainbow row
        return total;
    }

    private float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private void drawButton(GuiGraphicsExtractor context, String label, float x1, float y1, float x2, boolean hovered, int alpha) {
        float y2 = y1 + getHeight();
        Color theme = GUI.INSTANCE.theme.get();
        RenderUtil.rect(context, x1, y1, x2, y2, theme.getRGB());
        if (hovered) RenderUtil.rect(context, x1, y1, x2, y2, new Color(255, 255, 255, 45).getRGB());
        RenderUtil.drawRectOutline(context, (int) x1, (int) y1, (int) x2, (int) y2, OUTLINE);
        float textX = x1 + (x2 - x1 - width(label)) / 2f;
        float textY = y1 - 1;
        drawString(context, label, textX, textY, new Color(255, 255, 255, alpha).getRGB(), alpha);
    }

    @Override
    public void render(GuiGraphicsExtractor context, int mx, int my, float delta, int alpha) {
        renderHover(context, mx, my, alpha);
        drawString(context, setting.getName(), getX() + PAD, getY(), new Color(255, 255, 255, alpha).getRGB(), alpha);

        float swX2 = getX() + getWidth() - PAD;
        float swX1 = swX2 - 12;
        float swY1 = getY() + 2;
        float swY2 = getY() + getHeight() - 2;
        RenderUtil.rect(context, swX1, swY1, swX2, swY2, setting.getRaw().getRGB());
        RenderUtil.drawRectOutline(context, (int) swX1, (int) swY1, (int) swX2, (int) swY2, OUTLINE);

        float ex = openAnim.to(isOpen() ? 1f : 0f, 25f);
        if (ex <= 0.001f) return;

        int extra = expandedTotal() - getHeight();
        int animatedBottom = (int) Math.ceil(getY() + getHeight() + extra * ex);
        context.enableScissor((int) (getX() - 1), (int) (getY() + getHeight()), (int) (getX() + getWidth() + 1), animatedBottom);

        int size = boxSize();
        float bx = boxX();
        float by = boxTop();
        float hx = hueX();

        if (dragColor || dragHue || dragAlpha) {
            if (dragHue) selectedColor[0] = clamp((my - by) / size);
            if (dragColor) {
                selectedColor[1] = clamp((mx - bx) / size);
                selectedColor[2] = 1f - clamp((my - by) / size);
            }
            int a = setting.getRaw().getAlpha();
            if (dragAlpha && setting.isAllowAlpha()) a = (int) (255 * clamp((mx - alphaLeft()) / (alphaRight() - alphaLeft())));
            Color c = new Color(Color.HSBtoRGB(selectedColor[0], selectedColor[1], selectedColor[2]));
            setting.set(new Color(c.getRed(), c.getGreen(), c.getBlue(), a));
        }

        RenderUtil.horizontalGradient(context, bx, by, bx + size, by + size, Color.WHITE, Color.getHSBColor(selectedColor[0], 1, 1));
        RenderUtil.verticalGradient(context, bx, by, bx + size, by + size, new Color(0, 0, 0, 0), Color.BLACK);
        RenderUtil.drawRectOutline(context, (int) bx, (int) by, (int) (bx + size), (int) (by + size), OUTLINE);
        float svx = bx + size * selectedColor[1];
        float svy = by + size * (1f - selectedColor[2]);
        RenderUtil.drawRectOutline(context, (int) (svx - 2), (int) (svy - 2), (int) (svx + 2), (int) (svy + 2), 0xFFFFFFFF);
        RenderUtil.drawRectOutline(context, (int) (svx - 3), (int) (svy - 3), (int) (svx + 3), (int) (svy + 3), OUTLINE);

        for (float i = 0; i < size; i += 0.5f)
            RenderUtil.rect(context, hx, by + i, hx + HUE_WIDTH, by + i + 0.5f, Color.getHSBColor(i / size, 1, 1).getRGB());
        RenderUtil.drawRectOutline(context, (int) hx, (int) by, (int) (hx + HUE_WIDTH), (int) (by + size), OUTLINE);
        float huey = by + size * selectedColor[0];
        RenderUtil.rect(context, hx - 1, huey - 1, hx + HUE_WIDTH + 1, huey + 1, -1);
        RenderUtil.drawRectOutline(context, (int) (hx - 1), (int) (huey - 1), (int) (hx + HUE_WIDTH + 1), (int) (huey + 1), OUTLINE);

        if (setting.isAllowAlpha()) {
            Color v = setting.getRaw();
            float ax1 = alphaLeft();
            float ax2 = alphaRight();
            float ay = alphaTop();
            RenderUtil.horizontalGradient(context, ax1, ay, ax2, ay + ALPHA_HEIGHT, new Color(v.getRed(), v.getGreen(), v.getBlue(), 0), new Color(v.getRed(), v.getGreen(), v.getBlue(), 255));
            RenderUtil.drawRectOutline(context, (int) ax1, (int) ay, (int) ax2, (int) (ay + ALPHA_HEIGHT), OUTLINE);
            float apos = ax1 + (ax2 - ax1) * (v.getAlpha() / 255f);
            RenderUtil.rect(context, apos - 1, ay - 1, apos + 1, ay + ALPHA_HEIGHT + 1, -1);
            RenderUtil.drawRectOutline(context, (int) (apos - 1), (int) (ay - 1), (int) (apos + 1), (int) (ay + ALPHA_HEIGHT + 1), OUTLINE);
        }

        float by0 = buttonsTop();
        float halfW = (getWidth() - PAD) / 2f;
        float copyX1 = getX();
        float copyX2 = getX() + halfW;
        float pasteX1 = copyX2 + PAD;
        float pasteX2 = getX() + getWidth();

        copyClick = mx >= copyX1 && mx <= copyX2 && my >= by0 && my <= by0 + getHeight();
        pasteClick = mx >= pasteX1 && mx <= pasteX2 && my >= by0 && my <= by0 + getHeight();
        drawButton(context, "Copy", copyX1, by0, copyX2, copyClick, alpha);
        drawButton(context, "Paste", pasteX1, by0, pasteX2, pasteClick, alpha);

        float rainbowY = by0 + getHeight() + PAD;
        rainbowClick = mx >= getX() && mx <= getX() + getWidth() && my >= rainbowY && my <= rainbowY + getHeight();
        drawButton(context, setting.isRainbow() ? "Rainbow: On" : "Rainbow: Off", getX(), rainbowY, getX() + getWidth(), rainbowClick, alpha);

        context.disableScissor();
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {
        if (isHovering(mx, my) && btn == 1) setOpen(!isOpen());
        if (btn != 0 || !isOpen()) return;

        int size = boxSize();
        float bx = boxX();
        float by = boxTop();
        float hx = hueX();

        if (isHovering(mx, my, (int) bx, (int) by, (int) (bx + size), (int) (by + size))) dragColor = true;
        if (isHovering(mx, my, (int) hx, (int) by, (int) (hx + HUE_WIDTH), (int) (by + size))) dragHue = true;
        if (setting.isAllowAlpha()) {
            float ay = alphaTop();
            if (isHovering(mx, my, (int) alphaLeft(), (int) ay, (int) alphaRight(), (int) (ay + ALPHA_HEIGHT))) dragAlpha = true;
        }

        if (copyClick) GUIScreen.setColorClipboard(setting.getRaw());
        if (pasteClick && GUIScreen.getColorClipboard() != null) {
            Color c = GUIScreen.getColorClipboard();
            setting.set(new Color(c.getRed(), c.getGreen(), c.getBlue(), setting.getRaw().getAlpha()));
            selectedColor = Color.RGBtoHSB(setting.getRaw().getRed(), setting.getRaw().getGreen(), setting.getRaw().getBlue(), null);
        }
        if (rainbowClick) setting.setRainbow(!setting.isRainbow());
    }

    @Override
    public void mouseReleased(int mx, int my, int btn) {
        dragHue = dragColor = dragAlpha = false;
    }

    @Override
    public int getTotal() {
        int extra = expandedTotal() - getHeight();
        return getHeight() + (int) Math.ceil(extra * openAnim.get());
    }
}
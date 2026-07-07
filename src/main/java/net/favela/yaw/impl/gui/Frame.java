package net.favela.yaw.impl.gui;

import lombok.Getter;
import lombok.Setter;
import net.favela.yaw.impl.gui.settings.Button;
import net.favela.yaw.impl.gui.settings.ModuleButton;
import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.util.animation.Anim;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

public class Frame {

    @Getter
    private final String name;
    @Setter
    @Getter
    private int x;
    @Setter
    @Getter
    private int y;
    private int x2;
    private int y2;
    private float itemHeight;
    @Getter
    public boolean open;
    public boolean drag;
    @Setter
    @Getter
    private boolean visible = true;
    private String searchQuery = "";

    private final Minecraft mc = Minecraft.getInstance();
    private final ArrayList<ModuleButton> modules = new ArrayList<>();
    private float scrollOffset = 0f;

    private final Anim openAnim = new Anim(0f);
    private final Anim scrollAnim = new Anim(0f);

    public Frame(Module.Category category, int x, int y) {
        this.name = category.getName();
        this.x = x;
        this.y = y;
        this.open = true;
        for (Module module : Manager.MODULE.getModulesByCategory(category))
            this.modules.add(new ModuleButton(module));
        this.modules.sort(Comparator.comparing(Button::getName));
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query == null ? "" : query;
    }

    private boolean isSearching() {
        return !searchQuery.isEmpty();
    }

    private boolean matchesSearch(ModuleButton b) {
        return b.getName().toLowerCase().contains(searchQuery.toLowerCase());
    }

    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, int alpha) {
        drag(mouseX, mouseY);
        boolean searching = isSearching();
        Color theme = GUI.INSTANCE.theme.get();

        float fullH = 0;
        for (ModuleButton b : modules) {
            if (b.isHidden()) continue;
            if (searching && !matchesSearch(b)) continue;
            fullH += b.getHeight() + b.getItemHeight() + 1f + (b.isOpen() ? 1 : 0);
        }
        if (searching && fullH == 0) return;

        float factor = openAnim.to((open || searching) ? 1f : 0f, 25f);
        boolean pyzo = GUI.INSTANCE.scrollMode.get() == GUI.ScrollMode.PYZO;
        float visibleModuleHeight = Math.min(GUI.INSTANCE.categoryHeight.getInt(), fullH);
        float frameContentHeight = visibleModuleHeight * factor;
        float animatedHeight = pyzo ? frameContentHeight : fullH * factor;
        float drawScroll = scrollAnim.to(scrollOffset, 30f);

        RenderUtil.rect(context,
                getX() + (GUI.INSTANCE.outline.get() ? -1 : 0), getY(),
                getX() + getWidth() + (GUI.INSTANCE.outline.get() ? 1 : 0),
                getY() + getHeight() + 2 + (GUI.INSTANCE.outline.get() ? 1 : 0) + animatedHeight,
                new Color(0, 0, 0, (int) (100 * (alpha / 255f))).getRGB());

        RenderUtil.rect(context, getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                new Color(theme.getRed(), theme.getGreen(), theme.getBlue(),
                        (int) (theme.getAlpha() * (alpha / 255f))).getRGB());

        drawString(context, name, x + 2, y + GUI.INSTANCE.getTextOffset(), new Color(255, 255, 255, alpha).getRGB());

        if (GUI.INSTANCE.showCount.get()) {
            int gray = new Color(170, 170, 170, alpha).getRGB();
            int white = new Color(255, 255, 255, alpha).getRGB();
            int bracketColor = gray;
            int numberColor = white;
            String leftBracket = "[";
            String numberText = Integer.toString(modules.size());
            String rightBracket = "]";
            float totalWidth = mc.font.width(leftBracket) + mc.font.width(numberText) + mc.font.width(rightBracket);
            float cx = getX() + getWidth() - totalWidth - 2f;
            float ty = y + GUI.INSTANCE.getTextOffset();
            drawString(context, leftBracket, cx, ty, bracketColor);
            cx += mc.font.width(leftBracket);
            drawString(context, numberText, cx, ty, numberColor);
            cx += mc.font.width(numberText);
            drawString(context, rightBracket, cx, ty, bracketColor);
        }

        if (GUI.INSTANCE.outline.get()) {
            int outlineCol = new Color(theme.getRed(), theme.getGreen(), theme.getBlue(), (int) (theme.getAlpha() * (alpha / 255f))).getRGB();
            RenderUtil.rect(context, getX() - 1, getY(), getX(), getY() + getHeight() + 2 + animatedHeight, outlineCol);
            RenderUtil.rect(context, getX() + getWidth(), getY(), getX() + getWidth() + 1, getY() + getHeight() + 2 + animatedHeight, outlineCol);
            RenderUtil.rect(context, getX() - 1, getY() + getHeight() + 2 + animatedHeight, getX() + getWidth() + 1, getY() + getHeight() + 3 + animatedHeight, outlineCol);
        }

        int scissorTop = getY() + getHeight() + 2;
        int scissorBottom = (int) Math.ceil(getY() + getHeight() + 2 + animatedHeight);
        boolean scissor = scissorBottom > scissorTop;
        if (scissor) context.enableScissor(getX(), scissorTop, getX() + getWidth(), scissorBottom);

        if (factor > 0.001f) {
            itemHeight = 0;
            for (ModuleButton b : modules) {
                if (b.isHidden()) continue;
                if (searching && !matchesSearch(b)) continue;
                b.setX(getX() + 1);
                b.setY(getY() + getHeight() + 2 + itemHeight - (pyzo ? drawScroll : 0));
                if (pyzo) {
                    float bh = b.getHeight() + b.getItemHeight() + 1f + (b.isOpen() ? 1 : 0);
                    if (b.getY() + bh >= getY() + getHeight() + 2 && b.getY() <= getY() + getHeight() + 2 + frameContentHeight) b.render(context, mouseX, mouseY, delta, alpha);
                } else b.render(context, mouseX, mouseY, delta, alpha);
                itemHeight += b.getHeight() + b.getItemHeight() + 1f + (b.isOpen() ? 1 : 0);
            }
        }

        if (scissor) context.disableScissor();

        if (factor > 0.001f)
            for (ModuleButton b : modules)
                if (!b.isHidden()) {
                    if (searching && !matchesSearch(b)) continue;b.renderTooltip(context, mouseX, mouseY);
                }
    }

    public void mouseClicked(int mx, int my, int btn) {
        if (isHovering(mx, my)) {
            if (btn == 0) {
                x2 = x - mx;
                y2 = y - my;
                drag = true;
            }

            if (btn == 1) {
                open = !open;
                if (!open && GUI.INSTANCE.scrollMode.get() == GUI.ScrollMode.PYZO) scrollOffset = 0;
            }
        }

        if (!open) return;
        if (GUI.INSTANCE.scrollMode.get() == GUI.ScrollMode.PYZO && !isHoveringOverModuleList(mx, my)) return;
        boolean searching = isSearching();
        for (ModuleButton b : modules) {
            if (b.isHidden()) continue;
            if (searching && !matchesSearch(b)) continue;
            b.mouseClicked(mx, my, btn);
        }
    }

    public void mouseReleased(int mx, int my, int btn) {
        if (btn == 0) drag = false;
        for (ModuleButton b : modules) if (!b.isHidden()) b.mouseReleased(mx, my, btn);
    }

    public void mouseScrolled(double mx, double my, double amount) {
        if (GUI.INSTANCE.scrollMode.get() == GUI.ScrollMode.PYZO && isHoveringOverModuleList(mx, my) && open) {
            float fullH = 0;
            for (ModuleButton b : modules) {
                if (b.isHidden()) continue;
                if (isSearching() && !matchesSearch(b)) continue;
                fullH += b.getHeight() + b.getItemHeight() + 1f + (b.isOpen() ? 1 : 0);
            }
            float maxScroll = Math.max(0, fullH - GUI.INSTANCE.categoryHeight.getInt());
            scrollOffset -= amount * GUI.INSTANCE.scrollSpeed.getInt();
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        }
    }

    public void onKeyPressed(int code) {
        if (open) for (ModuleButton b : modules) if (!b.isHidden()) b.onKeyPressed(code);
    }

    public void keyReleased(int code) {
        if (open) for (ModuleButton b : modules) if (!b.isHidden()) b.keyReleased(code);
    }

    public void charTyped(char c, int m) {
        if (open) for (ModuleButton b : modules) if (!b.isHidden()) b.onCharTyped(c, m);
    }

    private void drag(int mx, int my) {
        if (drag) {
            x = x2 + mx;
            y = y2 + my;
        }
    }

    private boolean isHovering(double mx, double my) {
        return mx >= x && mx <= x + getWidth() && my >= y && my <= y + getHeight();
    }

    private boolean isHoveringOverModuleList(double mx, double my) {
        float fullH = 0;
        for (ModuleButton b : modules) {
            if (b.isHidden()) continue;
            if (isSearching() && !matchesSearch(b)) continue;
            fullH += b.getHeight() + b.getItemHeight() + 1f + (b.isOpen() ? 1 : 0);
        }

        float visible = Math.min(GUI.INSTANCE.categoryHeight.getInt(), fullH);
        return mx >= getX() && mx <= getX() + getWidth()
                && my >= getY() + getHeight() + 2
                && my <= getY() + getHeight() + 2 + visible;
    }

    private void drawString(GuiGraphicsExtractor ctx, String s, float x, float y, int color) {
        ctx.text(mc.font, s, (int) x, (int) y, color, true);
    }

    public int getWidth() {
        return 100 + GUI.INSTANCE.width.getInt();
    }

    public int getHeight() {
        return 12 + GUI.INSTANCE.height.getInt();
    }

}
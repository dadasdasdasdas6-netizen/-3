package net.favela.yaw.impl.gui.hud;

import net.favela.yaw.impl.gui.Frame;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.favela.yaw.impl.util.animation.Anim;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.ArrayList;

public class HudEditorScreen extends Screen {

    private static HudEditorScreen INSTANCE;

    private final ArrayList<Frame> frames = new ArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();

    public Hud currentDragging;
    public boolean anyHover;
    private float dragX;
    private float dragY;
    private boolean dragging;

    private final Anim screenAnim = new Anim(0f);

    private HudEditorScreen() {
        super(Component.literal("favelayaw-hudeditor"));
        load();
    }

    public static HudEditorScreen getInstance() {
        if (INSTANCE == null) INSTANCE = new HudEditorScreen();
        return INSTANCE;
    }

    private void load() {
        int x = (mc.getWindow().getGuiScaledWidth() - (104 + GUI.INSTANCE.width.getInt())) / 2;
        frames.add(new Frame(Module.Category.HUD, x, 50));
    }

    @Override
    public void onClose() {
        dragging = false;
        currentDragging = null;
        screenAnim.set(0f);
        super.onClose();
        if (HUD.getInstance() != null && HUD.getInstance().isEnabled()) HUD.getInstance().disable();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (mc.level != null && GUI.INSTANCE.blur.get()) {
            mc.options.menuBackgroundBlurriness().set(5);
            graphics.blurBeforeThisStratum();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        anyHover = false;
        float fade = screenAnim.to(1f, 20f);
        int alpha = (int) (255 * fade);

        Color grad = GUI.INSTANCE.backgroundGradient.get();
        RenderUtil.verticalGradient(context, 0, 0, context.guiWidth(), context.guiHeight(), new Color(0, 0, 0, 0), new Color(grad.getRed(), grad.getGreen(), grad.getBlue(), (int) (grad.getAlpha() * fade)));

        float centerX = context.guiWidth() / 2f;
        float centerY = context.guiHeight() / 2f;
        RenderUtil.rect(context, centerX - 0.5f, 0, centerX + 0.5f, context.guiHeight(), new Color(255, 255, 255, (int) (90 * fade)).getRGB());
        RenderUtil.rect(context, 0, centerY - 0.5f, context.guiWidth(), centerY + 0.5f, new Color(255, 255, 255, (int) (90 * fade)).getRGB());

        if (dragging && currentDragging != null) {
            updateModulePosition(mouseX, mouseY);
        }

        for (Hud module : Hud.getHudModules()) {
            if (!module.isEnabled()) continue;
            module.render(context);
        }

        for (Frame frame : frames) {
            if (!frame.isVisible()) continue;
            frame.render(context, mouseX, mouseY, delta, alpha);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (event.button() == 0) {
            int mouseX = (int) event.x();
            int mouseY = (int) event.y();
            for (Hud module : Hud.getHudModules()) {
                if (!module.isEnabled()) continue;
                if (!module.isHovering(mouseX, mouseY)) continue;
                currentDragging = module;
                dragX = mouseX - module.getX();
                dragY = mouseY - module.getY();
                dragging = true;
                return true;
            }
            currentDragging = null;
        }
        for (Frame frame : frames) {
            if (!frame.isVisible()) continue;
            frame.mouseClicked((int) event.x(), (int) event.y(), event.button());
        }
        return super.mouseClicked(event, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        for (Frame frame : frames) frame.mouseReleased((int) event.x(), (int) event.y(), event.button());
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        for (Frame frame : frames) {
            if (GUI.INSTANCE.scrollMode.get() == GUI.ScrollMode.PYZO) {
                frame.mouseScrolled(mouseX, mouseY, vertical);
            } else {
                frame.setY((int) (frame.getY() + vertical * 25.0));
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        for (Frame frame : frames) {
            if (!frame.isOpen()) continue;
            if (keyCode == 265) frame.setY(frame.getY() - 10);
            if (keyCode == 264) frame.setY(frame.getY() + 10);
            if (keyCode == 263) frame.setX(frame.getX() - 10);
            if (keyCode == 262) frame.setX(frame.getX() + 10);
            frame.onKeyPressed(keyCode);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        for (Frame frame : frames) frame.keyReleased(event.key());
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (Frame frame : frames) frame.charTyped((char) event.codepoint(), 0);
        return super.charTyped(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void updateModulePosition(int mouseX, int mouseY) {
        Hud module = currentDragging;
        int offset = HUD.getInstance() != null ? HUD.getInstance().offset.getInt() : 2;
        float scaledWidth = mc.getWindow().getGuiScaledWidth();
        float scaledHeight = mc.getWindow().getGuiScaledHeight();
        float width = module.getWidth();
        float height = module.getHeight();

        float x = (mouseX - dragX - offset) / (scaledWidth - width - 2 * offset);
        float y = (mouseY - dragY - offset) / (scaledHeight - height - 2 * offset);

        x = Math.max(0.0f, Math.min(1.0f, x));
        y = Math.max(0.0f, Math.min(1.0f, y));

        float snapThreshold = 0.025f;
        x = applySnapping(x, snapThreshold);
        y = applySnapping(y, snapThreshold);

        module.setPosX(x);
        module.setPosY(y);
    }

    private float applySnapping(float value, float snapThreshold) {
        if (value < snapThreshold) return 0.0f;
        if (value > 1.0f - snapThreshold) return 1.0f;
        if (Math.abs(value - 0.5f) < snapThreshold) return 0.5f;
        return value;
    }
}
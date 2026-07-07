package net.favela.yaw.impl.gui;

import lombok.Getter;
import lombok.Setter;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.util.animation.Anim;
import net.favela.yaw.impl.util.models.Timer;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;

public class GUIScreen extends Screen {

    private static GUIScreen INSTANCE;
    public final ArrayList<Frame> frames = new ArrayList<>();
    private boolean flag = false;
    private final Timer timer = new Timer();
    @Setter
    @Getter
    private static Color colorClipboard = null;
    private final Minecraft mc = Minecraft.getInstance();
    private final String[] trayButtonNames = new String[]{"combat", "misc", "movement", "render", "player", "client"};
    private final Map<String, Identifier> texturesOff = new HashMap<>();
    private final Map<String, Identifier> texturesOn = new HashMap<>();
    private final Map<String, Boolean> buttonStates = new HashMap<>();
    private float trayX;
    private float trayY;
    private boolean searchActive = false;
    private String searchQuery = "";

    private final Anim screenAnim = new Anim(0f);
    private boolean closing = false;

    private GUIScreen() {
        super(Component.literal("GUIModule"));
        load();
    }

    public static GUIScreen getInstance() {
        if (INSTANCE == null) INSTANCE = new GUIScreen();
        return INSTANCE;
    }

    private void load() {
        int totalWidth = (104 + GUI.INSTANCE.width.getInt()) * 6 + 3 * 5;
        int x = (mc.getWindow().getGuiScaledWidth() - totalWidth) / 2;
        x -= 60;
        if (x < 5) x = 5;
        for (Module.Category category : Module.Category.values()) {
            if (category == Module.Category.HUD) continue;
            frames.add(new Frame(category, x, 50));
            x += 104 + GUI.INSTANCE.width.getInt() + 3;
            buttonStates.put(category.name().toLowerCase(), true);
        }
    }

    @Override
    public void onClose() {
        if (closing) return;
        closing = true;
        searchActive = false;
        searchQuery = "";
        applySearch();
    }

    private void finishClose() {
        closing = false;
        screenAnim.set(0f);
        super.onClose();
        if (GUI.INSTANCE.isEnabled()) GUI.INSTANCE.toggle();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (mc.level != null && GUI.INSTANCE.blur.get()) {
            mc.options.menuBackgroundBlurriness().set(5);
            graphics.blurBeforeThisStratum();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        float fade = screenAnim.to(closing ? 0f : 1f, 20f);
        int alpha = (int) (255 * fade);
        if (GUI.INSTANCE.darken.get()) {
            int darkAlpha = (int) (GUI.INSTANCE.darkenStrength.getInt() * fade);
            RenderUtil.rect(graphics, 0, 0, graphics.guiWidth(), graphics.guiHeight(), new Color(0, 0, 0, darkAlpha).getRGB());
        }
        Color grad = GUI.INSTANCE.backgroundGradient.get();
        RenderUtil.verticalGradient(graphics, 0, 0, graphics.guiWidth(), graphics.guiHeight(),
                new Color(0, 0, 0, 0),
                new Color(grad.getRed(), grad.getGreen(), grad.getBlue(), (int) (grad.getAlpha() * fade)));
        if (timer.passedMs(500)) {
            flag = !flag;
            timer.reset();
        }
        trayX = (graphics.guiWidth() - trayButtonNames.length * 16) / 2f;
        trayY = graphics.guiHeight() - 16;
        for (Frame frame : frames) {
            if (!frame.isVisible()) continue;
            frame.render(graphics, mouseX, mouseY, delta, alpha);
        }
        renderSearchHint(graphics, alpha);
        if (searchActive) renderSearchBar(graphics, alpha);
        if (closing && fade <= 0.01f) finishClose();
    }

    private void renderSearchHint(GuiGraphicsExtractor context, int alpha) {
        if (searchActive) return;
        int color = new Color(100, 100, 100, alpha).getRGB();
        int textY = context.guiHeight() - mc.font.lineHeight - 2;
        context.text(mc.font, "Press Ctrl + F to search modules", 2, textY, color, true);
    }

    private void renderSearchBar(GuiGraphicsExtractor context, int alpha) {
        int barW = 180;
        int barH = 12 + GUI.INSTANCE.height.getInt();
        int barX = (context.guiWidth() - barW) / 2;
        int barY = 16;
        Color theme = GUI.INSTANCE.theme.get();
        RenderUtil.rect(context, barX - 1, barY - 1, barX + barW + 1, barY + barH + 1,
                new Color(theme.getRed(), theme.getGreen(), theme.getBlue(), (int) (theme.getAlpha() * (alpha / 255f))).getRGB());
        RenderUtil.rect(context, barX, barY, barX + barW, barY + barH, new Color(0, 0, 0, (int) (200 * (alpha / 255f))).getRGB());
        String display = searchQuery + (flag ? "_" : "");
        context.text(mc.font, display, barX + 3, barY + GUI.INSTANCE.getTextOffset(), new Color(255, 255, 255, alpha).getRGB(), true);
        if (searchQuery.isEmpty()) {
            context.text(mc.font, "search...", barX + 3, barY + GUI.INSTANCE.getTextOffset(), new Color(120, 120, 120, alpha).getRGB(), true);
        }
    }

    private void applySearch() {
        for (Frame frame : frames) frame.setSearchQuery(searchQuery);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (closing) return true;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == 0) {
            for (int i = 0; i < trayButtonNames.length; i++) {
                String name = trayButtonNames[i];
                float buttonX = trayX + i * 16;
                float buttonY = trayY;
                if (mouseX < buttonX || mouseX > buttonX + 16 || mouseY < buttonY || mouseY > buttonY + 16) continue;
                boolean newState = !buttonStates.getOrDefault(name, false);
                buttonStates.put(name, newState);
                for (Frame frame : frames) {
                    if (!frame.getName().toLowerCase().equals(name)) continue;
                    frame.setVisible(newState);
                }
                return true;
            }
        }
        for (Frame frame : frames) {
            if (!frame.isVisible()) continue;
            frame.mouseClicked((int) mouseX, (int) mouseY, button);
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        for (Frame frame : frames) frame.mouseReleased((int) event.x(), (int) event.y(), event.button());
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_F) {
            long handle = mc.getWindow().handle();
            boolean ctrl = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            if (ctrl) {
                searchActive = !searchActive;
                if (!searchActive) {
                    searchQuery = "";
                    applySearch();
                }
                return true;
            }
        }
        if (searchActive) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchActive = false;
                searchQuery = "";
                applySearch();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                applySearch();
                return true;
            }
            return true;
        }
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
        if (searchActive) {
            char c = (char) event.codepoint();
            if (c >= 32 && c < 127) {
                searchQuery += c;
                applySearch();
            }
            return true;
        }
        for (Frame frame : frames) frame.charTyped((char) event.codepoint(), 0);
        return super.charTyped(event);
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
    public boolean isPauseScreen() {
        return false;
    }

    public String getSym() {
        return flag ? "_" : "";
    }
}
package net.favela.yaw.impl.modules.categories.client;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.HudEditorScreen;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.NumberSetting;

import java.awt.Color;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class HUD extends Module {
    private static HUD INSTANCE;

    public NumberSetting offset = num("Offset", 0, 5, 2);

    public HUD() {
        super("HUD", "Edit HUD element positions", Category.CLIENT);
        INSTANCE = this;
    }

    public static HUD getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (GUI.INSTANCE.isEnabled()) GUI.INSTANCE.disable();
        MC.gui.setScreen(HudEditorScreen.getInstance());
        disable();
    }

    public Color getColor(int i) {
        Color theme = GUI.INSTANCE.theme.get();
        return new Color(theme.getRed(), theme.getGreen(), theme.getBlue(), 255);
    }
}
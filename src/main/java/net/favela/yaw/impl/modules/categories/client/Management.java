package net.favela.yaw.impl.modules.categories.client;

import com.google.auto.service.AutoService;
import lombok.Getter;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.ColorSetting;

import java.awt.Color;

@AutoService(Module.class)
public class Management extends Module {

    @Getter
    private static Management instance;

    public ColorSetting nameColor;
    public ColorSetting versionColor;

    public Management() {
        super("Management", "Title screen branding settings", Category.CLIENT);
        instance = this;

        Color defaultNameColor = (GUI.INSTANCE != null) ? GUI.INSTANCE.theme.get() : new Color(163, 135, 255, 255);

        nameColor = color("Name Color", "Color of client name on title screen", defaultNameColor,true);
        versionColor = color("Version Color","Color of version on title screen", Color.WHITE,true);

        this.enable();
    }

}
package net.favela.yaw.impl.modules.categories.misc;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.StringSetting;

@AutoService(Module.class)
public class DeathScreen extends Module {

    public static DeathScreen INSTANCE;

    public BooleanSetting noDelay = register(new BooleanSetting("NoDelay", "Removes the death-screen delay", () -> isEnabled(), false));
    public BooleanSetting customText = register(new BooleanSetting("CustomText", "Use a custom death message", () -> isEnabled(), false));
    public StringSetting text = register(new StringSetting("Text", "Custom death message", () -> isEnabled() && customText.get(), "oh no it was lag"));
    public BooleanSetting customScore = register(new BooleanSetting("CustomScore", "Use a custom score text", () -> isEnabled(), false));
    public StringSetting score = register(new StringSetting("Score", "Custom score text", () -> isEnabled() && customScore.get(), "u got all swagg"));

    public DeathScreen() {
        super("DeathScreen", "Edits ur death-screen", Category.MISC);
        INSTANCE = this;
    }
}
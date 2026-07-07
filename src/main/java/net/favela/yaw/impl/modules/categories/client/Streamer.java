package net.favela.yaw.impl.modules.categories.client;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.StringSetting;

@AutoService(Module.class)
public class Streamer extends Module {

    public StringSetting name = str("Name", "Player");

    public Streamer() {
        super("Streamer", "Replaces your real name in chat, tab, and messages", Category.CLIENT);
    }
}
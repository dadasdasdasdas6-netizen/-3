package net.favela.yaw.impl.management;

import lombok.Getter;
import net.favela.yaw.impl.management.managers.*;

public class Manager {

    public static ModuleManager MODULE;
    public static CommandManager COMMAND;
    public static ConfigManager CONFIG;
    public static KeybindManager KEYBIND;

    @Getter
    private static volatile boolean initialized = false;

    private Manager() {
    }

    public static synchronized void init() {
        if (initialized) return;

        MODULE = new ModuleManager();
        COMMAND = new CommandManager();
        CONFIG = new ConfigManager();
        KEYBIND = new KeybindManager();

        MODULE.initialize();
        COMMAND.initialize();
        CONFIG.load();
        CONFIG.registerLifecycle();
        KEYBIND.initialize();

        initialized = true;
    }
}
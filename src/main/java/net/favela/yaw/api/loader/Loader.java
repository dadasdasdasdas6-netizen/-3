package net.favela.yaw.api.loader;

import net.favela.yaw.impl.management.Manager;

/**
 * Client entry point: invoked from {@link net.favela.yaw.EntryPoint}
 * and hands control over to {@link net.favela.yaw.impl.management.Manager#init()}.
 *
 * <p>Project map for a quick start:
 *
 * @see net.favela.yaw.EntryPoint                                Fabric ClientModInitializer, the very first entry point
 * @see net.favela.yaw.impl.management.Manager                   central manager registry + initialization order
 * @see net.favela.yaw.impl.management.managers.ModuleManager    module registration and storage
 * @see net.favela.yaw.impl.management.managers.CommandManager   chat commands and their dispatcher (prefix "!")
 * @see net.favela.yaw.impl.management.managers.ConfigManager    saving/loading configs
 * @see net.favela.yaw.impl.management.managers.KeybindManager   handling module keybinds
 * @see net.favela.yaw.impl.modules.Module                       base module class (enable/disable, settings)
 * @see net.favela.yaw.impl.setting.Setting                      base setting class
 * @see net.favela.yaw.impl.event.Events                         event bus (TickEvent, RenderEvent, etc.)
 * @see net.favela.yaw.impl.commands.Command                     base command class
 * @see net.favela.yaw.api.wrapper.Wrapper                       quick access to Minecraft (MC) and wrappers
 * @see net.favela.yaw.impl.util.log.Log                         logging
 * @see net.favela.yaw.impl.util.identifier.Identifiers          Identifier factory
 */
public final class Loader {

    public static void load() {
        Manager.init();
    }
}
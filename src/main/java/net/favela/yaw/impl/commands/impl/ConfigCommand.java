package net.favela.yaw.impl.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.favela.yaw.impl.commands.Command;
import net.favela.yaw.impl.management.Manager;
import net.favela.yaw.impl.util.chat.ChatUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "Manage configs", "cfg");
    }

    @Override
    public void register(CommandDispatcher<SharedSuggestionProvider> dispatcher, String name) {
        dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal(name)
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("list")
                        .executes(context -> list()))
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("current")
                        .executes(context -> current()))
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("save")
                        .executes(context -> save(Manager.CONFIG.getCurrentConfig()))
                        .then(builder("name", StringArgumentType.string())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(Manager.CONFIG.list().stream(), b))
                                .executes(context -> save(StringArgumentType.getString(context, "name")))))
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("load")
                        .then(builder("name", StringArgumentType.string())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(Manager.CONFIG.list().stream(), b))
                                .executes(context -> load(StringArgumentType.getString(context, "name")))))
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("create")
                        .then(builder("name", StringArgumentType.string())
                                .executes(context -> create(StringArgumentType.getString(context, "name")))))
                .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("delete")
                        .then(builder("name", StringArgumentType.string())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(Manager.CONFIG.list().stream(), b))
                                .executes(context -> delete(StringArgumentType.getString(context, "name")))))
                .executes(context -> usage()));
    }

    private int usage() {
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Usage: " + ChatFormatting.WHITE + Manager.COMMAND.getPrefix() + "config <list|current|save|load|create|delete> [name]");
        return 1;
    }

    private int list() {
        List<String> configs = Manager.CONFIG.list();
        if (configs.isEmpty()) {
            ChatUtil.sendInfo("No configs found");
            return 1;
        }
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Configs (" + configs.size() + "):");
        String current = Manager.CONFIG.getCurrentConfig();
        for (String name : configs) {
            boolean active = name.equalsIgnoreCase(current);
            ChatUtil.sendMessage("  " + (active ? ChatFormatting.GREEN : ChatFormatting.WHITE) + name + (active ? ChatFormatting.GRAY + " (active)" : ""));
        }
        return 1;
    }

    private int current() {
        ChatUtil.sendInfo(ChatFormatting.GRAY + "Current config: " + ChatFormatting.WHITE + Manager.CONFIG.getCurrentConfig());
        return 1;
    }

    private int save(String name) {
        Manager.CONFIG.save(name);
        Manager.CONFIG.setCurrentConfig(name);
        ChatUtil.sendInfo(ChatFormatting.GREEN + "Saved config " + ChatFormatting.WHITE + name);
        return 1;
    }

    private int load(String name) {
        if (!Manager.CONFIG.exists(name)) {
            ChatUtil.sendError("Config not found: " + name);
            return 0;
        }
        if (Manager.CONFIG.load(name)) {
            ChatUtil.sendInfo(ChatFormatting.GREEN + "Loaded config " + ChatFormatting.WHITE + name);
            return 1;
        }
        ChatUtil.sendError("Failed to load config: " + name);
        return 0;
    }

    private int create(String name) {
        if (Manager.CONFIG.exists(name)) {
            ChatUtil.sendError("Config already exists: " + name);
            return 0;
        }
        if (Manager.CONFIG.create(name)) {
            ChatUtil.sendInfo(ChatFormatting.GREEN + "Created config " + ChatFormatting.WHITE + name);
            return 1;
        }
        ChatUtil.sendError("Failed to create config: " + name);
        return 0;
    }

    private int delete(String name) {
        if (!Manager.CONFIG.exists(name)) {
            ChatUtil.sendError("Config not found: " + name);
            return 0;
        }
        if (Manager.CONFIG.delete(name)) {
            ChatUtil.sendInfo(ChatFormatting.GREEN + "Deleted config " + ChatFormatting.WHITE + name);
            return 1;
        }
        ChatUtil.sendError("Failed to delete config: " + name);
        return 0;
    }
}
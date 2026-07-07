package net.favela.yaw.impl.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Getter;
import net.favela.yaw.impl.util.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

public class Command {

    @Getter
    private final String name;
    @Getter
    private final String description;
    private final List<String> aliases;

    public Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = List.of(aliases);
    }

    public void executeBuild(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {}

    public final void registerTo(CommandDispatcher<SharedSuggestionProvider> dispatcher) {
        register(dispatcher, getName());
        for (String alias : aliases) register(dispatcher, alias);
    }

    public void register(CommandDispatcher<SharedSuggestionProvider> dispatcher, String name) {
        LiteralArgumentBuilder<SharedSuggestionProvider> builder = LiteralArgumentBuilder.literal(name);
        executeBuild(builder);
        dispatcher.register(builder);
    }

    public static void sendMessage(String message) {
        if (MC.player == null) return;
        ChatUtil.sendMessage(message);
    }

    protected static <T> RequiredArgumentBuilder<ClientSuggestionProvider, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<ClientSuggestionProvider> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> builder(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
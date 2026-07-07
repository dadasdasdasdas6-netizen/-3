package net.favela.yaw.impl.util.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatUtil {
    public static void sendMessage(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(message));
        }
    }

    public static void sendSilentMessage(Component message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(message);
        }
    }

    public static void sendMessagePrefixID(String message, int id) {
        sendMessage(message);
    }

    public static void sendMessagePrefixID(String message) {
        sendMessage(message);
    }

    public static void sendInfo(String message) {
        sendMessage("§7[§bINFO§7] §f" + message);
    }

    public static void sendError(String message) {
        sendMessage("§7[§cERROR§7] §f" + message);
    }

    public static void senderrror(String message) {
        sendError(message);
    }
}
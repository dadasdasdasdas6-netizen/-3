package net.favela.yaw.impl.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.favela.yaw.impl.commands.Command;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.Setting;
import net.favela.yaw.impl.setting.settings.*;
import net.favela.yaw.impl.util.chat.ChatUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;

public class ModuleCommand extends Command {

    private final Module module;

    public ModuleCommand(Module module) {
        super(module.getName(), "");
        this.module = module;
    }

    @Override
    public void register(com.mojang.brigadier.CommandDispatcher<SharedSuggestionProvider> dispatcher, String name) {
        dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal(name)
                .then(builder("setting", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                module.getSettings().stream().map(Setting::getName),
                                builder
                        ))
                        .executes(context -> {
                            String settingName = StringArgumentType.getString(context, "setting");
                            Setting<?> setting = findSetting(settingName);
                            if (setting == null) {
                                ChatUtil.sendError("Setting not found");
                                return 0;
                            }
                            return displaySettingInfo(module, setting);
                        })
                        .then(builder("value", StringArgumentType.greedyString()).executes(context -> {
                                    String settingName = StringArgumentType.getString(context, "setting");
                                    Setting<?> setting = findSetting(settingName);
                                    if (setting == null) {
                                        ChatUtil.sendError("Setting not found");
                                        return 0;
                                    }
                                    String value = StringArgumentType.getString(context, "value");
                                    return setSetting(setting, value);
                                })
                        )
                )
        );
    }

    private Setting<?> findSetting(String settingName) {
        return module.getSettings().stream()
                .filter(s -> s.getName().equalsIgnoreCase(settingName))
                .findFirst()
                .orElse(null);
    }

    private int displaySettingInfo(Module module, Setting<?> setting) {
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Module: " + ChatFormatting.WHITE + module.getName());
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Setting: " + ChatFormatting.WHITE + setting.getName());
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Description: " + ChatFormatting.WHITE + setting.getDescription());
        ChatUtil.sendMessage(ChatFormatting.GRAY + "Current value: " + ChatFormatting.GREEN + getSettingValue(setting));

        if (setting instanceof NumberSetting numSetting) {
            ChatUtil.sendMessage(ChatFormatting.GRAY + "Range: " + ChatFormatting.WHITE + numSetting.getMin() + " - " + numSetting.getMax());
            ChatUtil.sendMessage(ChatFormatting.GRAY + "Step: " + ChatFormatting.WHITE + numSetting.getStep());
        } else if (setting instanceof EnumSetting<?> enumSetting) {
            StringBuilder values = new StringBuilder();
            for (Enum<?> value : enumSetting.getValues()) {
                values.append(value.name()).append(", ");
            }
            ChatUtil.sendMessage(ChatFormatting.GRAY + "Values: " + ChatFormatting.WHITE + values.substring(0, values.length() - 2));
        }
        return 1;
    }

    private int setSetting(Setting<?> setting, String value) {
        try {
            switch (setting) {
                case BooleanSetting boolSetting -> {
                    boolean newValue = Boolean.parseBoolean(value);
                    boolSetting.setValue(newValue);
                    ChatUtil.sendInfo(ChatFormatting.GREEN + "Set " + ChatFormatting.WHITE + setting.getName() + ChatFormatting.GREEN + " to " + ChatFormatting.WHITE + newValue);
                }
                case NumberSetting numSetting -> {
                    Number newValue = switch (numSetting.get()) {
                        case Integer ignored -> Integer.parseInt(value);
                        case Double ignored -> Double.parseDouble(value);
                        case Float ignored -> Float.parseFloat(value);
                        case null, default -> Long.parseLong(value);
                    };
                    numSetting.set(newValue);
                    ChatUtil.sendInfo(ChatFormatting.GREEN + "Set " + ChatFormatting.WHITE + setting.getName() + ChatFormatting.GREEN + " to " + ChatFormatting.WHITE + numSetting.get());
                }
                case StringSetting strSetting -> {
                    strSetting.set(value);
                    ChatUtil.sendInfo(ChatFormatting.GREEN + "Set " + ChatFormatting.WHITE + setting.getName() + ChatFormatting.GREEN + " to " + ChatFormatting.WHITE + value);
                }
                case EnumSetting<?> enumSetting -> {
                    if (!applyEnum(enumSetting, value)) return 0;
                }
                case BindSetting bindSetting -> {
                    try {
                        int keyCode = Integer.parseInt(value);
                        bindSetting.setKey(keyCode);
                        ChatUtil.sendInfo(ChatFormatting.GREEN + "Set " + ChatFormatting.WHITE + setting.getName() + ChatFormatting.GREEN + " to key code " + ChatFormatting.WHITE + keyCode);
                    } catch (NumberFormatException e) {
                        ChatUtil.sendError("Invalid key code. Please provide an integer.");
                        return 0;
                    }
                }
                case SetSetting<?> setSetting -> {
                    return applySet(setSetting, value);
                }
                default -> {
                    ChatUtil.sendError("Unsupported setting type: " + setting.getClass().getSimpleName());
                    return 0;
                }
            }
            return 1;
        } catch (Exception e) {
            ChatUtil.sendError("Error setting value: " + e.getMessage());
            return 0;
        }
    }

    private <E extends Enum<E>> boolean applyEnum(EnumSetting<E> setting, String value) {
        try {
            E enumValue = Enum.valueOf(setting.get().getDeclaringClass(), value.toUpperCase());
            setting.set(enumValue);
            ChatUtil.sendInfo(ChatFormatting.GREEN + "Set " + ChatFormatting.WHITE + setting.getName() + ChatFormatting.GREEN + " to " + ChatFormatting.WHITE + enumValue.name());
            return true;
        } catch (IllegalArgumentException e) {
            ChatUtil.sendError("Invalid enum value. Valid values: ");
            for (Enum<?> v : setting.getValues()) {
                ChatUtil.sendMessage("  " + ChatFormatting.WHITE + v.name());
            }
            return false;
        }
    }

    private <T> int applySet(SetSetting<T> setting, String value) {
        String[] parts = value.split(" ", 2);
        String action = parts[0].toLowerCase();

        if (action.equals("clear")) {
            setting.clear();
            ChatUtil.sendInfo(ChatFormatting.GREEN + "Cleared " + ChatFormatting.WHITE + setting.getName());
            return 1;
        }

        if (parts.length != 2 || !(action.equals("add") || action.equals("remove") || action.equals("toggle"))) {
            ChatUtil.sendInfo("Usage: add <value>, remove <value>, toggle <value>, or clear");
            return 0;
        }

        T item = parseValue(setting.getType(), parts[1]);
        if (item == null) {
            ChatUtil.sendError("Failed to parse value");
            return 0;
        }

        switch (action) {
            case "add" -> {
                setting.add(item);
                ChatUtil.sendInfo(ChatFormatting.GREEN + "Added " + ChatFormatting.WHITE + parts[1] + ChatFormatting.GREEN + " to " + ChatFormatting.WHITE + setting.getName());
            }
            case "remove" -> {
                setting.remove(item);
                ChatUtil.sendInfo(ChatFormatting.GREEN + "Removed " + ChatFormatting.WHITE + parts[1] + ChatFormatting.GREEN + " from " + ChatFormatting.WHITE + setting.getName());
            }
            case "toggle" -> {
                setting.toggle(item);
                ChatUtil.sendInfo(ChatFormatting.GREEN + "Toggled " + ChatFormatting.WHITE + parts[1] + ChatFormatting.GREEN + " in " + ChatFormatting.WHITE + setting.getName());
            }
        }
        return 1;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T parseValue(Class<T> type, String value) {
        try {
            if (type == String.class) return (T) value;
            if (type == Integer.class) return (T) Integer.valueOf(value);
            if (type == Double.class) return (T) Double.valueOf(value);
            if (type == Float.class) return (T) Float.valueOf(value);
            if (type == Long.class) return (T) Long.valueOf(value);
            if (type == Boolean.class) return (T) Boolean.valueOf(value);
            if (type.isEnum()) return (T) Enum.valueOf((Class<Enum>) type, value.toUpperCase());
        } catch (Exception ignored) {
        }
        return null;
    }

    private String getSettingValue(Setting<?> setting) {
        return switch (setting) {
            case BooleanSetting boolSetting -> String.valueOf(boolSetting.get());
            case NumberSetting numSetting -> numSetting.getRenderText();
            case StringSetting strSetting -> strSetting.get();
            case EnumSetting<?> enumSetting -> enumSetting.get().name();
            case BindSetting bindSetting -> String.valueOf(bindSetting.getKey());
            case SetSetting<?> setSetting -> setSetting.get().toString();
            default -> "unknown";
        };
    }
}
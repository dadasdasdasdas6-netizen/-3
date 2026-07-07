package net.favela.yaw.impl.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.favela.yaw.impl.setting.Setting;
import net.favela.yaw.impl.util.log.Log;

import java.util.function.BooleanSupplier;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final Class<E> enumClass;

    public EnumSetting(String name, String description, BooleanSupplier visibility, E defaultValue) {
        super(name, description, visibility, defaultValue);
        this.enumClass = defaultValue.getDeclaringClass();
    }

    public EnumSetting(String name, String description, E defaultValue) {
        this(name, description, null, defaultValue);
    }

    public E[] getValues() {
        return enumClass.getEnumConstants();
    }

    public void setNext() {
        E[] values = getValues();
        set(values[(get().ordinal() + 1) % values.length]);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get().name());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) return;
        try {
            set(Enum.valueOf(enumClass, element.getAsString()));
        } catch (IllegalArgumentException e) {
            Log.warn("Unknown enum value '{}' for setting {}", element.getAsString(), getName());
            reset();
        }
    }
}
package net.favela.yaw.impl.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.favela.yaw.impl.setting.Setting;

import java.util.function.BooleanSupplier;

public class StringSetting extends Setting<String> {

    public StringSetting(String name, String description, BooleanSupplier visibility, String defaultValue) {
        super(name, description, visibility, defaultValue);
    }

    public StringSetting(String name, String description, String defaultValue) {
        super(name, description, null, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsString());
        }
    }
}
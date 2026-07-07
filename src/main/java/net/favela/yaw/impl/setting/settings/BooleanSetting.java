package net.favela.yaw.impl.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.favela.yaw.impl.setting.Setting;

import java.util.function.BooleanSupplier;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, String description, BooleanSupplier visibility, boolean defaultValue) {
        super(name, description, visibility, defaultValue);
    }

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, null, defaultValue);
    }

    public void setValue(boolean value) {
        set(value);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsBoolean());
        }
    }
}
package net.favela.yaw.impl.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.favela.yaw.impl.setting.Setting;
import net.favela.yaw.impl.util.keyboard.Keybinding;

import java.util.function.BooleanSupplier;

public class BindSetting extends Setting<Keybinding> {

    private final int defaultKey;

    public BindSetting(String name, String description, BooleanSupplier visibility, int defaultKey) {
        super(name, description, visibility, new Keybinding(defaultKey));
        this.defaultKey = defaultKey;
    }

    public BindSetting(String name, String description, boolean visibility, int defaultKey) {
        this(name, description, () -> visibility, defaultKey);
    }

    public BindSetting(String name, String description, int defaultKey) {
        this(name, description, (BooleanSupplier) null, defaultKey);
    }

    public void setValue(Keybinding keybinding) {
        set(keybinding);
    }

    public int getKey() {
        return get().get();
    }

    public void setKey(int key) {
        get().setKey(key);
    }

    @Override
    public Keybinding getDefaultValue() {
        return new Keybinding(defaultKey);
    }

    @Override
    public void reset() {
        set(new Keybinding(defaultKey));
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getKey());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setKey(element.getAsInt());
        }
    }
}
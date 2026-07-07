package net.favela.yaw.impl.setting;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import net.favela.yaw.impl.modules.Module;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {

    @Getter private final String name;
    @Getter private final String description;
    @Setter private BooleanSupplier visibility;
    @Getter @Setter private Module module;

    protected T value;
    @Getter
    protected final T defaultValue;

    protected Setting(String name, String description, BooleanSupplier visibility, T defaultValue) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public T get() { return value; }

    public void set(T value) { this.value = value; }

    public void reset() { this.value = defaultValue; }

    public boolean isVisible() {
        return visibility == null || visibility.getAsBoolean();
    }

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonElement element);
}
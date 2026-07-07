package net.favela.yaw.impl.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.favela.yaw.impl.setting.Setting;

import java.awt.Color;
import java.util.function.BooleanSupplier;

public class ColorSetting extends Setting<Color> {

    private static final float RAINBOW_SPEED = 4000f;

    @Getter @Setter private boolean allowAlpha;
    @Getter @Setter private boolean rainbow;
    private final float hueOffset;

    public ColorSetting(String name, String description, BooleanSupplier visibility, Color defaultValue, boolean allowAlpha) {
        super(name, description, visibility, defaultValue);
        this.allowAlpha = allowAlpha;
        this.hueOffset = (Math.abs(name.hashCode()) % 1000) / 1000f;
    }

    public ColorSetting(String name, String description, Color defaultValue, boolean allowAlpha) {
        this(name, description, null, defaultValue, allowAlpha);
    }

    public ColorSetting(String name, String description, Color defaultValue) {
        this(name, description, null, defaultValue, true);
    }

    public Color getRaw() {
        return value;
    }

    @Override
    public Color get() {
        if (!rainbow) return value;
        float base = (System.currentTimeMillis() % (long) RAINBOW_SPEED) / RAINBOW_SPEED;
        float hue = (base + hueOffset) % 1f;
        Color c = Color.getHSBColor(hue, 1f, 1f);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), allowAlpha ? value.getAlpha() : 255);
    }

    public int getRGB() {
        return get().getRGB();
    }

    @Override
    public void reset() {
        super.reset();
        this.rainbow = false;
    }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("rgba", value.getRGB());
        obj.addProperty("rainbow", rainbow);
        return obj;
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null) return;
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("rgba")) this.value = new Color(obj.get("rgba").getAsInt(), true);
            if (obj.has("rainbow")) this.rainbow = obj.get("rainbow").getAsBoolean();
        } else if (element.isJsonPrimitive()) {
            this.value = new Color(element.getAsInt(), true);
        }
    }
}
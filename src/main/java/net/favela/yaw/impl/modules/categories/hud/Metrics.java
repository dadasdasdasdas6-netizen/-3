package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.EnumSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Metrics extends Hud {

    public BooleanSetting fps = bool("FPS", true);
    public BooleanSetting ping = bool("Ping", true);
    public BooleanSetting tps = bool("TPS", true);
    public BooleanSetting speed = bool("Speed", true);
    public EnumSetting<SpeedUnit> speedUnit = enm("Notation", () -> speed.get(), SpeedUnit.BLOCKS);
    public BooleanSetting time = bool("Time", true);
    public EnumSetting<TimeFormat> timeFormat = enm("Format", () -> time.get(), TimeFormat.HH_MM_SS);
    public BooleanSetting brand = bool("Brand", true);
    public BooleanSetting heldDurabil = bool("Durability", true);
    public NumberSetting lineSpacing = num("LineSpacing", -15f, 20f, 0f);

    private final DecimalFormat df = new DecimalFormat("0.0");

    public Metrics() {
        super("Metrics", "FPS, TPS, Ping, Speed, Time, Brand, Durability", 5, 5);
    }

    private List<String[]> buildEntries() {
        List<String[]> list = new ArrayList<>();

        if (fps.get())
            list.add(new String[]{"FPS:", String.valueOf(MC.getFps())});

        if (ping.get()) {
            int lat = 0;
            if (MC.getConnection() != null && MC.player != null) {
                PlayerInfo info = MC.getConnection().getPlayerInfo(MC.player.getUUID());
                if (info != null) lat = info.getLatency();
            }
            list.add(new String[]{"Ping:", lat + "ms"});
        }

        if (tps.get())
            list.add(new String[]{"TPS:", df.format(getTps())});

        if (speed.get() && MC.player != null) {
            Vec3 d = MC.player.getDeltaMovement();
            double bps = Math.sqrt(d.x * d.x + d.z * d.z) * 20.0;
            String text = switch (speedUnit.get()) {
                case KMH -> df.format(bps * 3.6) + " km/h";
                case MS -> df.format(bps) + " m/s";
                default -> df.format(bps) + " b/s";
            };
            list.add(new String[]{"Speed:", text});
        }

        if (time.get()) {
            DateTimeFormatter fmt = switch (timeFormat.get()) {
                case HH_MM -> DateTimeFormatter.ofPattern("HH:mm");
                default -> DateTimeFormatter.ofPattern("HH:mm:ss");
            };
            list.add(new String[]{"Time:", LocalTime.now().format(fmt)});
        }

        if (brand.get()) {
            String serverText = MC.getSingleplayerServer() != null ? "Singleplayer" : (MC.getConnection() != null ? MC.getConnection().serverBrand() : null);
            if (serverText != null && !serverText.isEmpty())
                list.add(new String[]{"Brand:", serverText});
        }

        if (heldDurabil.get() && MC.player != null && MC.player.getMainHandItem().isDamageableItem()) {
            var stack = MC.player.getMainHandItem();
            int pct = (stack.getMaxDamage() - stack.getDamageValue()) * 100 / stack.getMaxDamage();
            list.add(new String[]{"Durability:", pct + "%"});
        }

        return list;
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        if (MC.player == null) {
            setWidth(0);
            setHeight(0);
            return;
        }

        List<String[]> lines = buildEntries();

        if (lines.isEmpty()) {
            setWidth(0);
            setHeight(0);
            return;
        }

        Font font = MC.font;
        float lineH = Math.max(font.lineHeight + lineSpacing.getFloat(), 1f);

        boolean bottomHalf = getPosY() > 0.5f;
        Comparator<String[]> byWidth = Comparator.comparingInt(en -> font.width(en[0] + " " + en[1]));
        lines.sort(bottomHalf ? byWidth : byWidth.reversed());

        int elementWidth = lines.stream().mapToInt(en -> font.width(en[0] + " " + en[1])).max().orElse(0);

        float baseX = getX();
        float baseY = getY();
        float xPos = getPosX();

        for (int i = 0; i < lines.size(); i++) {
            String label = lines.get(i)[0];
            String value = lines.get(i)[1];
            int lineWidth = font.width(label + " " + value);

            int xFix;
            if (xPos > 0.5f) xFix = elementWidth - lineWidth;
            else if (xPos == 0.5f) xFix = elementWidth / 2 - lineWidth / 2;
            else xFix = 0;

            float lineX = baseX + xFix;
            float lineY = baseY + i * lineH;
            float valueX = lineX + font.width(label + " ");

            context.text(font, label, (int) lineX, (int) lineY, gradientColor(i), true);
            context.text(font, value, (int) valueX, (int) lineY, 0xFFFFFFFF, true);
        }

        setWidth(elementWidth);
        setHeight((int)(lines.size() * lineH));
    }

    private int gradientColor(int i) {
        HUD hed = HUD.getInstance();
        if (hed != null) return hed.getColor(i).getRGB();
        return GUI.INSTANCE != null ? GUI.INSTANCE.theme.getRGB() : 0xFFA387FF;
    }

    private double getTps() {
        return 20.0;
    }

    public enum SpeedUnit { BLOCKS, KMH, MS }
    public enum TimeFormat { HH_MM_SS, HH_MM }
}
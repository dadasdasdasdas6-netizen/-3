package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class TextRadar extends Hud {

    private final NumberSetting limit = num("Limit", "Max players shown", 1, 20, 6);
    private final BooleanSetting health = bool("Health", "Show health", true);
    private final BooleanSetting dist = bool("Distance", "Show distance", true);
    private final BooleanSetting valueColor = bool("Value Color", "Color the distance value", true);
    private final BooleanSetting meters = bool("Meters", "Append 'm' to distance", true);
    private final BooleanSetting totems = bool("Pops", "Show totem pops", true);

    private final Map<UUID, Integer> pops = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> lastTotemCount = new ConcurrentHashMap<>();

    public TextRadar() {
        super("TextRadar", "Displays nearby players", 5, 60);
    }

    @Override
    public void render(GuiGraphicsExtractor ctx) {
        super.render(ctx);
        if (MC.level == null || MC.player == null) return;

        trackTotems();

        ArrayList<Player> players = new ArrayList<>(MC.level.players());
        players.sort(Comparator.comparingDouble(p -> MC.player.distanceToSqr(p)));

        ArrayList<String> lines = new ArrayList<>();
        int maxWidth = 0;
        int playerCount = 0;

        for (Player p : players) {
            if (p == MC.player) continue;

            double distance = Math.sqrt(MC.player.distanceToSqr(p));
            int popCount = getPops(p);

            String healthText = String.format("%.0f", p.getHealth() + p.getAbsorptionAmount());
            String nameText = p.getName().getString();
            String distanceText = String.format("%.0f", distance) + (meters.get() ? "m" : "");
            String totemText = "-" + popCount;

            StringBuilder line = new StringBuilder();
            if (health.get()) {
                line.append(getHealthColor((int) p.getHealth()))
                        .append(healthText)
                        .append(ChatFormatting.RESET)
                        .append(" ");
            }
            line.append(nameText);
            if (dist.get()) {
                line.append(" ")
                        .append(valueColor.get() ? getDistColor((int) distance).toString() : "")
                        .append(distanceText)
                        .append(ChatFormatting.RESET);
            }
            if (popCount > 0 && totems.get()) {
                line.append(" ")
                        .append(getTotemColor(popCount))
                        .append(totemText)
                        .append(ChatFormatting.RESET);
            }

            String text = line.toString();
            lines.add(text);
            int width = MC.font.width(text);
            if (width > maxWidth) maxWidth = width;
            if (++playerCount >= limit.getInt()) break;
        }

        int lineH = MC.font.lineHeight;
        float x = getX();
        float y = getY();

        for (int i = 0; i < lines.size(); i++) {
            String string = lines.get(i);
            int lineWidth = MC.font.width(string);
            int xFix = maxWidth - lineWidth;
            int color = resolveColor(i).getRGB();
            ctx.text(MC.font, string, (int) (x + xFix), (int) (y + i * lineH), color, true);
        }

        setWidth(maxWidth);
        setHeight(lineH * lines.size());
    }

    private void trackTotems() {
        for (Player p : new ArrayList<>(MC.level.players())) {
            UUID uid = p.getUUID();
            int cur = countTotems(p);
            Integer last = lastTotemCount.get(uid);
            if (last != null && last - cur > 0) {
                pops.merge(uid, last - cur, Integer::sum);
            }
            lastTotemCount.put(uid, cur);
        }
    }

    private Color resolveColor(int i) {
        HUD hud = HUD.getInstance();
        if (hud != null) return hud.getColor(i);
        return GUI.INSTANCE != null ? GUI.INSTANCE.theme.get() : Color.WHITE;
    }

    private ChatFormatting getHealthColor(int health) {
        if (health > 18) return ChatFormatting.GREEN;
        if (health > 16) return ChatFormatting.DARK_GREEN;
        if (health > 12) return ChatFormatting.YELLOW;
        if (health > 8) return ChatFormatting.GOLD;
        if (health > 4) return ChatFormatting.RED;
        return ChatFormatting.DARK_RED;
    }

    private ChatFormatting getTotemColor(int totems) {
        if (totems > 0) {
            if (totems <= 3) return ChatFormatting.YELLOW;
            if (totems <= 5) return ChatFormatting.GOLD;
            if (totems <= 9) return ChatFormatting.RED;
            return ChatFormatting.DARK_RED;
        }
        return ChatFormatting.RESET;
    }

    private ChatFormatting getDistColor(int value) {
        if (value <= 3) return ChatFormatting.RED;
        if (value <= 9) return ChatFormatting.GOLD;
        return ChatFormatting.GREEN;
    }

    private int getPops(Player p) {
        return pops.getOrDefault(p.getUUID(), 0);
    }

    private int countTotems(Player p) {
        int count = 0;
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() == Items.TOTEM_OF_UNDYING) count++;
        }
        if (p.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) count++;
        return count;
    }
}
package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.event.Events;
import net.favela.yaw.impl.event.events.PacketEvent;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.GUI;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Packets extends Hud {

    private int inCount;
    private int outCount;
    private int inPerSec;
    private int outPerSec;
    private long lastUpdate = System.currentTimeMillis();

    public Packets() {
        super("Packets", "Displays the incoming and outgoing packets", 200, 275);

        Events.on(PacketEvent.Receive.class, e -> inCount++);
        Events.on(PacketEvent.Send.class, e -> outCount++);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        if (MC.player == null || MC.level == null) return;

        long now = System.currentTimeMillis();
        if (now - lastUpdate >= 1000L) {
            inPerSec = inCount;
            outPerSec = outCount;
            inCount = 0;
            outCount = 0;
            lastUpdate = now;
        }

        Font font = MC.font;
        float x = getX();
        float y = getY();

        String out = String.valueOf(outPerSec);
        String rest = "<-" + inPerSec;

        float cursorX = x;
        for (int i = 0; i < out.length(); i++) {
            String ch = String.valueOf(out.charAt(i));
            HUD editor = HUD.getInstance();
            Color charColor = editor != null ? editor.getColor(i) : (GUI.INSTANCE != null ? GUI.INSTANCE.theme.get() : new Color(0xA387FF));
            int argb = 0xFF000000 | (charColor.getRGB() & 0x00FFFFFF);
            context.text(font, ch, (int) cursorX, (int) y, argb, true);
            cursorX += font.width(ch);
        }

        context.text(font, ChatFormatting.WHITE + rest, (int) cursorX, (int) y, 0xFFFFFFFF, true);

        setWidth(font.width(out + rest));
        setHeight(font.lineHeight);
    }
}
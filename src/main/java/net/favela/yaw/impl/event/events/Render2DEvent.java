package net.favela.yaw.impl.event.events;

import net.favela.yaw.impl.event.Event;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public record Render2DEvent(GuiGraphicsExtractor context, float delta) implements Event {

}
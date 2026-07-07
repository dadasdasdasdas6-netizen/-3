package net.favela.yaw.impl.modules.categories.render;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.event.events.Render2DEvent;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Crosshair extends Module {

    public static Crosshair INSTANCE;

    private final NumberSetting thickness = num("Thickness", 0.5f, 5.0f, 1.0f);
    private final NumberSetting length = num("Length", 1.0f, 15.0f, 2.6f);
    private final NumberSetting gap = num("Gap", 0.0f, 10.0f, 1.1f);
    private final NumberSetting opacity = num("Opacity", 0.0f, 1.0f, 0.5f);
    private final BooleanSetting top = bool("Top", false);
    private final BooleanSetting bottom = bool("Bottom", true);
    private final BooleanSetting left = bool("Left", true);
    private final BooleanSetting right = bool("Right", true);
    private final BooleanSetting dot = bool("Dot", false);
    private final BooleanSetting blockColorDot = bool("BlockColorDot", false);

    public Crosshair() {
        super("Crosshair", "Simple crosshair with block-aware dot", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (MC.player == null || MC.level == null) return;

        GuiGraphicsExtractor context = event.context();
        if (context == null) return;

        int scaledWidth = MC.getWindow().getGuiScaledWidth();
        int scaledHeight = MC.getWindow().getGuiScaledHeight();
        int cx = scaledWidth / 2;
        int cy = scaledHeight / 2;

        int t = Math.max(1, Math.round(thickness.getFloat()));
        int half = t / 2;
        int l = Math.max(1, Math.round(length.getFloat()));
        int g = Math.max(0, Math.round(gap.getFloat()));

        int crossColor = argb(255, 255, 255);

        if (top.get()) {
            fill(context, cx - half, cy - l - g, cx + half + 1, cy - g, crossColor);
        }
        if (bottom.get()) {
            fill(context, cx - half, cy + g + 1, cx + half + 1, cy + g + l + 1, crossColor);
        }
        if (left.get()) {
            fill(context, cx - g - l, cy - half, cx - g, cy + half + 1, crossColor);
        }
        if (right.get()) {
            fill(context, cx + g + 1, cy - half, cx + g + l + 1, cy + half + 1, crossColor);
        }

        if (dot.get()) {
            int dotColor;
            if (blockColorDot.get()) {
                HitResult hit = MC.player.pick(20.0, 0.0f, false);
                if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                    BlockState state = MC.level.getBlockState(pos);
                    dotColor = (state.is(Blocks.OBSIDIAN) || state.is(Blocks.BEDROCK))
                            ? argb(255, 0, 0) : argb(0, 255, 0);
                } else {
                    dotColor = argb(0, 255, 0);
                }
            } else {
                dotColor = argb(255, 255, 255);
            }
            int dotSize = Math.max(1, half);
            fill(context, cx - dotSize, cy - dotSize, cx + dotSize + 1, cy + dotSize + 1, dotColor);
        }
    }

    private void fill(GuiGraphicsExtractor ctx, int x0, int y0, int x1, int y1, int color) {
        int minX = Math.min(x0, x1);
        int maxX = Math.max(x0, x1);
        int minY = Math.min(y0, y1);
        int maxY = Math.max(y0, y1);
        ctx.fill(minX, minY, maxX, maxY, color);
    }

    private int argb(int r, int g, int b) {
        float o = Math.max(0.0f, Math.min(1.0f, opacity.getFloat()));
        int a = Math.round(o * 255.0f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
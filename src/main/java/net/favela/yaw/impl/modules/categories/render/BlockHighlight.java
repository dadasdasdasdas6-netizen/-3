package net.favela.yaw.impl.modules.categories.render;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.vertex.PoseStack;
import net.favela.yaw.impl.event.events.Render3DEvent;
import net.favela.yaw.impl.event.events.RenderBlockOutlineEvent;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.ColorSetting;
import net.favela.yaw.impl.setting.settings.EnumSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.favela.yaw.impl.util.render.RenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class BlockHighlight extends Module {
    public static BlockHighlight instance;

    public final EnumSetting<Mode> mode = enm("Mode", "Render mode", Mode.Fill);
    public final ColorSetting color = color("Color", "Highlight color", new Color(163, 135, 255, 255), true);
    public final NumberSetting lineWidth = num("LineWidth", "Outline width", 0.1f, 5.0f, 1.0f);
    public final NumberSetting fillAlpha = num("FillAlpha", () -> mode.get() != Mode.Outline, 0, 255, 40);
    public final EnumSetting<AnimationMode> animation = enm("Animation", "Animation mode", AnimationMode.EaseOut);
    public final NumberSetting animationTime = num("AnimTime", () -> animation.get() != AnimationMode.None, 0, 1000, 150);

    private AABB startBox = null;
    private AABB currentBox = null;
    private AABB targetBox = null;
    private AABB fadeOutBox = null;
    private AABB fadeInBox = null;
    private BlockPos lastPos = null;
    private long animationStart = 0L;

    public BlockHighlight() {
        super("BlockHighlight", "Draws box at the block that you are looking at", Category.RENDER);
        instance = this;
    }

    @Override
    public void onDisable() {
        resetAnimation();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (MC.level == null || MC.player == null) return;

        BlockPos targetPos = null;
        AABB targetBoxRaw = null;
        if (MC.hitResult instanceof BlockHitResult result && result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            VoxelShape shape = MC.level.getBlockState(pos).getShape(MC.level, pos);
            if (!shape.isEmpty()) {
                targetPos = pos;
                targetBoxRaw = shape.bounds().move(pos);
            }
        }

        List<RenderTask> tasks = new ArrayList<>();
        if (targetPos == null || targetBoxRaw == null) {
            resetAnimation();
        } else {
            updateAnimation(targetPos, targetBoxRaw);
            if (animation.get() == AnimationMode.Fade) {
                addFadeTasks(tasks);
            } else {
                AABB renderBox = getRenderBox();
                if (renderBox != null) {
                    tasks.add(new RenderTask(renderBox, 1.0f));
                }
            }
        }

        if (tasks.isEmpty()) return;
        for (RenderTask t : tasks) {
            renderBox(event.getMatrix(), t.box, t.factor);
        }
    }

    @Override
    public void onRenderBlockOutline(RenderBlockOutlineEvent event) {
        event.cancel();
    }

    private void updateAnimation(BlockPos pos, AABB newTarget) {
        AnimationMode m = animation.get();
        if (m == AnimationMode.None || animationTime.getInt() <= 0) {
            startBox = newTarget;
            currentBox = newTarget;
            targetBox = newTarget;
            fadeOutBox = null;
            fadeInBox = newTarget;
            lastPos = pos;
            animationStart = System.currentTimeMillis();
            return;
        }
        if (targetBox == null || lastPos == null || !lastPos.equals(pos)) {
            if (m == AnimationMode.Fade) {
                fadeOutBox = targetBox;
                fadeInBox = newTarget;
                startBox = newTarget;
                currentBox = newTarget;
                targetBox = newTarget;
            } else {
                startBox = currentBox == null ? newTarget : currentBox;
                targetBox = newTarget;
                fadeOutBox = null;
                fadeInBox = null;
            }
            lastPos = pos;
            animationStart = System.currentTimeMillis();
        }
    }

    private AABB getRenderBox() {
        if (targetBox == null) {
            return null;
        }
        if (animation.get() == AnimationMode.None || animationTime.getInt() <= 0) {
            currentBox = targetBox;
            return currentBox;
        }
        if (startBox == null) {
            startBox = targetBox;
        }
        long now = System.currentTimeMillis();
        double progress = (double) (now - animationStart) / (double) animationTime.getInt();
        progress = clamp(progress, 0.0, 1.0);
        double eased = ease(progress, animation.get());
        currentBox = lerp(startBox, targetBox, eased);
        if (progress >= 1.0) {
            currentBox = targetBox;
            startBox = targetBox;
        }
        return currentBox;
    }

    private void addFadeTasks(List<RenderTask> tasks) {
        if (targetBox == null) {
            return;
        }
        if (animationTime.getInt() <= 0) {
            tasks.add(new RenderTask(targetBox, 1.0f));
            fadeOutBox = null;
            fadeInBox = targetBox;
            currentBox = targetBox;
            return;
        }
        long now = System.currentTimeMillis();
        double progress = (double) (now - animationStart) / (double) animationTime.getInt();
        progress = clamp(progress, 0.0, 1.0);
        double eased = ease(progress, AnimationMode.EaseOut);
        double fadeOutAlpha = 1.0 - eased;
        if (fadeOutBox != null && fadeOutAlpha > 0.01) {
            tasks.add(new RenderTask(fadeOutBox, (float) fadeOutAlpha));
        }
        if (fadeInBox != null && eased > 0.01) {
            tasks.add(new RenderTask(fadeInBox, (float) eased));
        }
        if (progress >= 1.0) {
            fadeOutBox = null;
            fadeInBox = targetBox;
            currentBox = targetBox;
        }
    }

    private void renderBox(PoseStack stack, AABB box, float factor) {
        Color base = color.get();
        Mode m = mode.get();
        if (m == Mode.Fill || m == Mode.Both) {
            int a = (int) (fillAlpha.getInt() * factor);
            RenderUtil.drawBoxFilled(stack, box, new Color(base.getRed(), base.getGreen(), base.getBlue(), clampAlpha(a)));
        }
        if (m == Mode.Outline || m == Mode.Both) {
            int a = (int) (base.getAlpha() * factor);
            RenderUtil.drawBoxOutline(stack, box, new Color(base.getRed(), base.getGreen(), base.getBlue(), clampAlpha(a)), lineWidth.getFloat());
        }
    }

    private int clampAlpha(int alpha) {
        return Math.max(0, Math.min(255, alpha));
    }

    private AABB lerp(AABB from, AABB to, double progress) {
        return new AABB(
                lerp(from.minX, to.minX, progress),
                lerp(from.minY, to.minY, progress),
                lerp(from.minZ, to.minZ, progress),
                lerp(from.maxX, to.maxX, progress),
                lerp(from.maxY, to.maxY, progress),
                lerp(from.maxZ, to.maxZ, progress)
        );
    }

    private double lerp(double from, double to, double progress) {
        return from + (to - from) * progress;
    }

    private double ease(double progress, AnimationMode m) {
        progress = clamp(progress, 0.0, 1.0);
        return switch (m) {
            case None, Linear, Fade -> progress;
            case EaseIn -> progress * progress * progress;
            case EaseOut -> {
                double inverse = 1.0 - progress;
                yield 1.0 - inverse * inverse * inverse;
            }
            case EaseInOut -> {
                if (progress < 0.5) {
                    yield 4.0 * progress * progress * progress;
                }
                double value = -2.0 * progress + 2.0;
                yield 1.0 - (value * value * value) / 2.0;
            }
            case Elastic -> {
                if (progress == 0.0 || progress == 1.0) {
                    yield progress;
                }
                double c4 = (2.0 * Math.PI) / 3.0;
                yield Math.pow(2.0, -10.0 * progress) * Math.sin((progress * 10.0 - 0.75) * c4) + 1.0;
            }
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void resetAnimation() {
        startBox = null;
        currentBox = null;
        targetBox = null;
        fadeOutBox = null;
        fadeInBox = null;
        lastPos = null;
        animationStart = 0L;
    }

    private record RenderTask(AABB box, float factor) {
    }

    public enum Mode {
        Outline,
        Fill,
        Both
    }

    public enum AnimationMode {
        None,
        Linear,
        EaseIn,
        EaseOut,
        EaseInOut,
        Elastic,
        Fade
    }
}
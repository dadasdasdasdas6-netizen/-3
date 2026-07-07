package net.favela.yaw.impl.modules.categories.render;

import java.awt.Color;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import net.favela.yaw.impl.event.events.Render2DEvent;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.ColorSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Tracers extends Module {

    public final ColorSetting colorSetting = color("Color", new Color(167, 123, 234, 255));
    public final NumberSetting scale = num("Scale", 0.1f, 5.0f, 3.0f);
    public final NumberSetting width = num("Width", 0.0f, 8.0f, 0.5f);
    public final NumberSetting offset = num("Offset", 20, 200, 30);
    public final BooleanSetting down = bool("Down", true);
    public final NumberSetting downH = num("DownHeight", 0.1f, 20.0f, 5.0f);
    public final BooleanSetting blink = bool("Blink", true);
    public final BooleanSetting inRender = bool("InRender", true);
    public final BooleanSetting ignoreNakeds = bool("IgnoreNakeds", false);

    private final Identifier arrowTexture = Identifier.fromNamespaceAndPath("favelayaw", "textures/client/arrow_triangle1.png");

    public Tracers() {
        super("Tracers", "Arrow tracers for nearby players", Category.RENDER);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (MC.player == null || MC.level == null) return;

        GuiGraphicsExtractor context = event.context();
        float cx = context.guiWidth() / 2.0f;
        float cy = context.guiHeight() / 2.0f;
        float playerYaw = MC.player.getYRot();

        for (AbstractClientPlayer e : Lists.newArrayList(MC.level.players())) {
            if (e == MC.player) continue;
            double distSq = e.position().subtract(MC.player.position()).horizontalDistanceSqr();
            if (distSq < 1.0E-4) continue;
            if (inRender.get() && isInFov(e)) continue;
            if (ignoreNakeds.get() && isNaked(e)) continue;

            float yaw = getRotationTo(e) - playerYaw;
            float size = scale.getFloat() * 5.0f;
            int off = offset.getInt();

            context.pose().pushMatrix();
            context.pose().translate(cx, cy);
            context.pose().rotate((float) Math.toRadians(yaw));
            context.pose().translate(0.0f, -off);
            if (down.get()) {
                context.pose().translate(0.0f, -downH.getFloat());
            }
            context.pose().translate(-size / 2.0f, -size / 2.0f);

            int alpha = colorSetting.get().getAlpha();
            if (blink.get()) {
                long time = System.currentTimeMillis() % 2000L;
                double factor = (Math.sin(time / 1000.0 * Math.PI) + 1.0) / 2.0;
                alpha = 100 + (int) (155.0 * factor);
            }

            Color base = colorSetting.get();
            int packed = (alpha << 24) | (base.getRed() << 16) | (base.getGreen() << 8) | base.getBlue();
            int iSize = Math.round(size);
            context.blit(RenderPipelines.GUI_TEXTURED, arrowTexture, 0, 0, 0.0f, 0.0f, iSize, iSize, iSize, iSize, iSize, iSize, packed);

            context.pose().popMatrix();
        }
    }

    private static float getRotationTo(Entity entity) {
        if (MC.player == null) return 0.0f;
        float partial = MC.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        double ex = lerp(entity.xOld, entity.getX(), partial);
        double ez = lerp(entity.zOld, entity.getZ(), partial);
        double px = lerp(MC.player.xOld, MC.player.getX(), partial);
        double pz = lerp(MC.player.zOld, MC.player.getZ(), partial);
        return (float) (-(Math.atan2(ex - px, ez - pz) * (180.0 / Math.PI)));
    }

    private static double lerp(double prev, double curr, float t) {
        return prev + (curr - prev) * t;
    }

    private boolean isInFov(AbstractClientPlayer target) {
        if (MC.player == null || MC.gameRenderer == null) return false;
        float halfFov = MC.options.fov().get() / 2.0f;
        Vec3 diff = target.position().subtract(MC.player.position());
        if (diff.lengthSqr() < 1.0E-4) return false;
        Vec3 toTarget = diff.normalize();
        double angle = Math.toDegrees(Math.acos(toTarget.dot(MC.player.getViewVector(1.0f))));
        return angle <= halfFov;
    }

    private static boolean isNaked(AbstractClientPlayer p) {
        return p.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && p.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                && p.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                && p.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }
}
package net.favela.yaw.impl.modules.categories.hud;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.gui.hud.Hud;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.modules.categories.client.HUD;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.Color;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Condition extends Hud {

    private double animatedAlpha = 255.0;
    private String lastStatus = "";

    public Condition() {
        super("Condition", "Litvin candicii", 50, 9);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        if (MC.player == null) return;

        Font font = MC.font;

        String currentStatus = getStatus();
        if (!currentStatus.equals(lastStatus)) {
            animatedAlpha = 0.0;
            lastStatus = currentStatus;
        }
        animatedAlpha += (255.0 - animatedAlpha) * 1.0;
        animatedAlpha = Math.max(0.0, Math.min(255.0, animatedAlpha));

        int x = (int) getX();
        int y = (int) getY();
        float xOffset = x;
        int a = (int) animatedAlpha;

        for (int i = 0; i < currentStatus.length(); ++i) {
            String ch = String.valueOf(currentStatus.charAt(i));
            Color base = HUD.getInstance().getColor(i);
            int col = new Color(base.getRed(), base.getGreen(), base.getBlue(), a).getRGB();
            context.text(font, ch, (int) xOffset, y, col, true);
            xOffset += font.width(ch);
        }

        setWidth(font.width(currentStatus));
        setHeight(font.lineHeight);
    }

    private String getStatus() {
        if (MC.player.getPose() == Pose.SLEEPING) return "sleeping";
        if (MC.player.getPose() == Pose.DYING) return "dead";
        if (MC.player.isInLava()) return "in lava";
        if (MC.player.isInWall()) return "walled";
        if (MC.player.getVehicle() != null) return "riding";
        if (MC.player.getPose() == Pose.FALL_FLYING) return "flying";
        if (MC.player.isUsingItem()) {
            ItemStack main = MC.player.getMainHandItem();
            ItemStack off = MC.player.getOffhandItem();
            if (main.is(Items.BOW) || off.is(Items.BOW)) return "aiming";
            if (main.is(Items.POTION) || off.is(Items.POTION)) return "drinking";
            return "eating";
        }
        if (MC.player.getPose() == Pose.SWIMMING) {
            if (MC.player.isInWater()) return "swimming";
            return "vodolaz";
        }
        if (MC.player.getPose() == Pose.CROUCHING && !MC.player.onGround()) return "crouch-air";
        if (MC.level.getBlockState(MC.player.blockPosition()).isSolidRender()) return "burrowed";
        if (MC.player.getPose() == Pose.CROUCHING) return "crouching";
        if (MC.player.isSprinting() && MC.player.onGround()) return "sprinting";

        double vy = MC.player.getDeltaMovement().y;
        if (vy < -0.1) return "falling";
        if (vy > 0.1) return "ascending";

        double hSpeed = Math.hypot(MC.player.getDeltaMovement().x, MC.player.getDeltaMovement().z);
        if (hSpeed > 0.01) return "walking";

        return "standing";
    }
}
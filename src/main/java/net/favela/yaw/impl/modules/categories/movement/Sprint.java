package net.favela.yaw.impl.modules.categories.movement;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.minecraft.world.effect.MobEffects;

import static net.favela.yaw.api.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (MC.player == null || MC.level == null) return;
        if (MC.player.isSpectator()) return;
        if (MC.player.isUsingItem()) return;
        if (MC.player.isShiftKeyDown()) return;
        if (MC.player.isCrouching()) return;
        if (MC.player.isPassenger()) return;
        if (MC.player.isFallFlying()) return;
        if (MC.player.isSleeping()) return;
        if (MC.player.getFoodData().getFoodLevel() <= 6 && !MC.player.getAbilities().mayfly) return;
        if (MC.player.hasEffect(MobEffects.BLINDNESS)) return;
        if (!MC.player.input.keyPresses.forward()) return;

        if (!MC.player.isSprinting()) {
            MC.player.setSprinting(true);
        }
    }
}
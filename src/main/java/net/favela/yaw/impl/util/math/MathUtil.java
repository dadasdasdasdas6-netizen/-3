package net.favela.yaw.impl.util.math;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

public class MathUtil {

    public static double[] directionSpeed(double speed) {
        float forward = MC.player.input.getMoveVector().y;
        float side = MC.player.input.getMoveVector().x;
        float yaw = MC.player.yRotO + (MC.player.getYRot() - MC.player.yRotO) * MC.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }
}
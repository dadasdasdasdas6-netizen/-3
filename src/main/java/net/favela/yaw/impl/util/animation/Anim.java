package net.favela.yaw.impl.util.animation;

public class Anim {

    private static final float EPSILON = 0.0015f;
    private static final float MAX_DT = 0.1f;
    private float value;
    private long last;

    public Anim(float initial) {
        this.value = initial;
        this.last = System.nanoTime();
    }

    public float get() {
        return value;
    }

    public void set(float v) {
        this.value = v;
        this.last = System.nanoTime();
    }

    public float to(float target, float speed) {
        float dt = consumeDelta();
        float factor = 1f - (float) Math.exp(-speed * dt);
        value += (target - value) * factor;
        if (Math.abs(target - value) < EPSILON) {
            value = target;
        }
        return value;
    }

    private float consumeDelta() {
        long now = System.nanoTime();
        float dt = (now - last) / 1.0E9f;
        last = now;
        if (dt < 0f) dt = 0f;
        if (dt > MAX_DT) dt = MAX_DT;
        return dt;
    }
}

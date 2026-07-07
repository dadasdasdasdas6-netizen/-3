package net.favela.yaw.impl.util.keyboard;

import lombok.Getter;
import lombok.Setter;

@Setter
public class Keybinding {

    private int key;
    @Getter
    private net.favela.yaw.impl.util.keyboard.Keybinding.bindType mode;

    public Keybinding(int key) {
        this.key = key;
        this.mode = net.favela.yaw.impl.util.keyboard.Keybinding.bindType.NORMAL;
    }

    public static net.favela.yaw.impl.util.keyboard.Keybinding none() {
        return new net.favela.yaw.impl.util.keyboard.Keybinding(-1);
    }

    public int get() {
        return this.key;
    }

    public enum bindType {
        NORMAL,
        HOLD
    }
}
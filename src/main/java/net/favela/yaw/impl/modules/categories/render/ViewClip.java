package net.favela.yaw.impl.modules.categories.render;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.BooleanSetting;
import net.favela.yaw.impl.setting.settings.NumberSetting;

@AutoService(Module.class)
public class ViewClip extends Module {

    public static ViewClip INSTANCE;

    public final NumberSetting range  = num("Range", 1.0f, 10.0f, 2.5f);
    public final BooleanSetting player = bool("Distance", true);

    public ViewClip() {
        super("ViewClip", "Change player camera distance", Category.RENDER);
        INSTANCE = this;
    }
}
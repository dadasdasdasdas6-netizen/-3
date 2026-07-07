package net.favela.yaw.impl.modules.categories.client;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.favela.yaw.impl.setting.settings.EnumSetting;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

@AutoService(Module.class)
public class Capes extends Module {

    private static Capes INSTANCE;

    public enum Cape {

        NONE("none"),
        NL("nl"),
        FUTURE("future"),
        IVANZOLO("ivanzolo"),
        MIO("mio"),
        THIGHS("thighs");

        public final String file;

        Cape(String file) {
            this.file = file;
        }
    }

    public final EnumSetting<Cape> selectedCape = enm("Cape", this::isEnabled, Cape.FUTURE);

    public Capes() {
        super("Capes", "Custom cape rendering", Category.CLIENT);
        INSTANCE = this;
    }

    public static Capes getInstance() {
        return INSTANCE;
    }

    public boolean hasCape() {
        return selectedCape.get() != Cape.NONE;
    }

    public ClientAsset.ResourceTexture getCapeTexture() {
        return new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("favelayaw", "capes/" + selectedCape.get().file));
    }
}
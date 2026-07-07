package net.favela.yaw.impl.modules.categories.combat;

import com.google.auto.service.AutoService;
import net.favela.yaw.impl.modules.Module;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

@AutoService(Module.class)
public class AutoTotem extends Module {

    public AutoTotem() {
        super("AutoTotem", "Automatically keeps a totem of undying in offhand", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (MC.player == null || MC.gameMode == null) return;

        if (MC.player.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.TOTEM_OF_UNDYING)) return;

        var inv = MC.player.getInventory();
        int containerId = MC.player.inventoryMenu.containerId;

        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                MC.gameMode.handleContainerInput(containerId, 36 + i, 40, ContainerInput.SWAP, MC.player);
                return;
            }
        }

        for (int i = 9; i < 36; i++) {
            if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                MC.gameMode.handleContainerInput(containerId, i, 40, ContainerInput.SWAP, MC.player);
                return;
            }
        }
    }
}
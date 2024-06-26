package radon.jujutsu_kaisen.client.gui.screen;


import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.client.gui.screen.radial.DisplayItem;
import radon.jujutsu_kaisen.client.gui.screen.radial.ItemStackDisplayItem;
import radon.jujutsu_kaisen.client.gui.screen.radial.RadialScreen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.network.packet.c2s.ShadowInventoryTakeC2SPacket;

import java.util.List;


public class ShadowInventoryScreen extends RadialScreen {
    @Override
    protected List<? extends DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return List.of();

        ITenShadowsData data = cap.getTenShadowsData();
        List<ItemStack> inventory = data.getShadowInventory();
        return inventory.stream().map(stack -> new ItemStackDisplayItem(this.minecraft, this, () ->
                PacketDistributor.sendToServer(new ShadowInventoryTakeC2SPacket((page * MAX_ITEMS) + inventory.indexOf(stack))), stack)).toList();
    }
}
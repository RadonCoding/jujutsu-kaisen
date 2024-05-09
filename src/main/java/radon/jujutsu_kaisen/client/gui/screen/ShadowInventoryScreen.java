package radon.jujutsu_kaisen.client.gui.screen;

import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.c2s.ShadowInventoryTakeC2SPacket;

import java.util.List;


public class ShadowInventoryScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return List.of();

        ITenShadowsData data = cap.getTenShadowsData();
        return data.getShadowInventory().stream().map(DisplayItem::new).toList();
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered == -1) return;

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        DisplayItem item = this.getCurrent().get(this.hovered);

        if (item.type == DisplayItem.Type.ITEM) {
            PacketDistributor.sendToServer(new ShadowInventoryTakeC2SPacket((page * MAX_ITEMS) + this.hovered));
        }
    }
}
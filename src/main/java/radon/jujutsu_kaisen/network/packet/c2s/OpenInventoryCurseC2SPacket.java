package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;

public class OpenInventoryCurseC2SPacket {
    public OpenInventoryCurseC2SPacket() {
    }

    public OpenInventoryCurseC2SPacket(FriendlyByteBuf ignored) {
    }

    public void encode(FriendlyByteBuf ignored) {

    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ItemStack stack = sender.getItemBySlot(EquipmentSlot.CHEST);

            if (stack.getItem() instanceof InventoryCurseItem item) {
                sender.openMenu(item);
            }
        });
        ctx.setPacketHandled(true);
    }
}
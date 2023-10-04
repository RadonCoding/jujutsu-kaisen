package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.function.Supplier;

public class ShadowInventoryTakeC2SPacket {
    private final int index;

    public ShadowInventoryTakeC2SPacket(int index) {
        this.index = index;
    }

    public ShadowInventoryTakeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                ItemStack stack = cap.getShadowInventory(this.index);

                if (player.getMainHandItem().isEmpty()) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, stack);
                } else {
                    if (!player.addItem(stack)) return;
                }
                cap.removeShadowInventory(this.index);

                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            });
        });
        ctx.setPacketHandled(true);
    }
}
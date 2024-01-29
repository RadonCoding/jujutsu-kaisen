package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

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
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ITenShadowsData cap = sender.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();

            ItemStack stack = cap.getShadowInventory(this.index);

            if (sender.getMainHandItem().isEmpty()) {
                sender.setItemSlot(EquipmentSlot.MAINHAND, stack);
            } else {
                if (!sender.addItem(stack)) return;
            }
            cap.removeShadowInventory(this.index);

            PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), sender);
        });
        ctx.setPacketHandled(true);
    }
}
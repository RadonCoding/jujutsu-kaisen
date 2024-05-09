package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

public record ShadowInventoryTakeC2SPacket(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShadowInventoryTakeC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "shadow_inventory_take_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ShadowInventoryTakeC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ShadowInventoryTakeC2SPacket::index,
            ShadowInventoryTakeC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ITenShadowsData data = cap.getTenShadowsData();

            ItemStack stack = data.getShadowInventory(this.index);

            if (sender.getMainHandItem().isEmpty()) {
                sender.setItemSlot(EquipmentSlot.MAINHAND, stack);
            } else {
                if (!sender.addItem(stack)) return;
            }
            data.removeShadowInventory(this.index);

            PacketDistributor.sendToPlayer(sender, new SyncTenShadowsDataS2CPacket(data.serializeNBT(sender.registryAccess())));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
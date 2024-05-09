package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;

public record SyncIdleTransfigurationDataS2CPacket(CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncIdleTransfigurationDataS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_idle_transfiguration_data_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncIdleTransfigurationDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncIdleTransfigurationDataS2CPacket::nbt,
            SyncIdleTransfigurationDataS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IIdleTransfigurationData data = cap.getIdleTransfigurationData();
            data.deserializeNBT(player.registryAccess(), this.nbt);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

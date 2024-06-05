package radon.jujutsu_kaisen.network.packet.s2c;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;

public record SyncDomainDataS2CPacket(ResourceKey<Level> dimension, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncDomainDataS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_domain_data_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncDomainDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            SyncDomainDataS2CPacket::dimension,
            ByteBufCodecs.COMPOUND_TAG,
            SyncDomainDataS2CPacket::nbt,
            SyncDomainDataS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IDomainData data = player.level().getData(JJKAttachmentTypes.DOMAIN);
            data.deserializeNBT(player.registryAccess(), this.nbt);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

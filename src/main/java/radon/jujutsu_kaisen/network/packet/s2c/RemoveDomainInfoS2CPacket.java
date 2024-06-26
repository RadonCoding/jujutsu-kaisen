package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
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

import java.util.UUID;

public record RemoveDomainInfoS2CPacket(ResourceKey<Level> dimension, UUID identifier) implements CustomPacketPayload {
    public static final Type<RemoveDomainInfoS2CPacket> TYPE = new Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_domain_info_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RemoveDomainInfoS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            RemoveDomainInfoS2CPacket::dimension,
            UUIDUtil.STREAM_CODEC,
            RemoveDomainInfoS2CPacket::identifier,
            RemoveDomainInfoS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            IDomainData data = player.level().getData(JJKAttachmentTypes.DOMAIN);
            data.remove(this.identifier);
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

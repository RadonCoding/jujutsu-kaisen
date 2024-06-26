package radon.jujutsu_kaisen.network.packet.s2c;


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
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.domain.DomainInfo;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;

import java.util.Optional;

public record UpdateDomainInfoS2CPacket(ResourceKey<Level> dimension, DomainInfo info) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateDomainInfoS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "update_domain_info_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UpdateDomainInfoS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            UpdateDomainInfoS2CPacket::dimension,
            DomainInfo.STREAM_CODEC,
            UpdateDomainInfoS2CPacket::info,
            UpdateDomainInfoS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (player.level().dimension() != this.dimension) return;

            Optional<IDomainData> data = DataProvider.getDataIfPresent(player.level(), JJKAttachmentTypes.DOMAIN);

            if (data.isEmpty()) return;

            data.get().update(this.info);
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

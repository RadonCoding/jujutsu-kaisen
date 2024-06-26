package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;

import java.util.UUID;

public record SyncMouthS2CPacket(UUID src) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncMouthS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_mouth_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncMouthS2CPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SyncMouthS2CPacket::src,
            SyncMouthS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> PerfectBodyVisual.onChant(this.src));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

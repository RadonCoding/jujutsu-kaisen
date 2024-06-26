package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import java.util.UUID;

public record SyncVisualDataS2CPacket(UUID src, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncVisualDataS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_visual_data_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncVisualDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SyncVisualDataS2CPacket::src,
            ByteBufCodecs.COMPOUND_TAG,
            SyncVisualDataS2CPacket::nbt,
            SyncVisualDataS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientVisualHandler.receive(this.src, this.nbt));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

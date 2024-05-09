package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import java.util.UUID;
import java.util.function.Supplier;

public record ReceiveVisualDataS2CPacket(UUID src, CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReceiveVisualDataS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "receive_visual_data_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ReceiveVisualDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ReceiveVisualDataS2CPacket::src,
            ByteBufCodecs.COMPOUND_TAG,
            ReceiveVisualDataS2CPacket::nbt,
            ReceiveVisualDataS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientVisualHandler.receive(this.src, this.nbt));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
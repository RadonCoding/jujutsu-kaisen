package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

public class ClearChantsS2CPacket implements CustomPacketPayload {
    public static final ClearChantsS2CPacket INSTANCE = new ClearChantsS2CPacket();

    public static final CustomPacketPayload.Type<ClearChantsS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "clear_chants_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ClearChantsS2CPacket> STREAM_CODEC = StreamCodec.unit(
            INSTANCE
    );

    private ClearChantsS2CPacket() {
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientChantHandler.remove());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
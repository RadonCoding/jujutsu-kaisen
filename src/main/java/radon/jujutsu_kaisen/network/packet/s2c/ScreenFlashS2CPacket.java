package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;

public class ScreenFlashS2CPacket implements CustomPacketPayload {
    public static final ScreenFlashS2CPacket INSTANCE = new ScreenFlashS2CPacket();

    public static final CustomPacketPayload.Type<ScreenFlashS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "screen_flash_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ScreenFlashS2CPacket> STREAM_CODEC = StreamCodec.unit(
            INSTANCE
    );

    private ScreenFlashS2CPacket() {
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(ScreenFlashOverlay::flash);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
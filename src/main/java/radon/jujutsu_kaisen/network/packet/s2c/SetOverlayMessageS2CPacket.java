package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;

public record SetOverlayMessageS2CPacket(Component component, boolean animate) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetOverlayMessageS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "set_overlay_message_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetOverlayMessageS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            SetOverlayMessageS2CPacket::component,
            ByteBufCodecs.BOOL,
            SetOverlayMessageS2CPacket::animate,
            SetOverlayMessageS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientWrapper.setOverlayMessage(this.component, this.animate));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
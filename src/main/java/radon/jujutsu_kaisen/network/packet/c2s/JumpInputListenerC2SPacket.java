package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.IControllableFlyingRide;

public record JumpInputListenerC2SPacket(boolean down) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<JumpInputListenerC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "jump_input_listener_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, JumpInputListenerC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            JumpInputListenerC2SPacket::down,
            JumpInputListenerC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.getVehicle() instanceof IControllableFlyingRide listener) {
                listener.setJump(this.down);
            } else if (sender.getFirstPassenger() instanceof IControllableFlyingRide listener) {
                listener.setJump(this.down);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
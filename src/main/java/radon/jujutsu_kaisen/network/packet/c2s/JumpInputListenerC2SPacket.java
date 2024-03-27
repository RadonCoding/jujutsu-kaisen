package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.base.IControllableFlyingRide;

public class JumpInputListenerC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "jump_input_listener_serverbound");

    private final boolean down;

    public JumpInputListenerC2SPacket(boolean down) {
        this.down = down;
    }

    public JumpInputListenerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.getVehicle() instanceof IControllableFlyingRide listener) {
                listener.setJump(this.down);
            } else if (sender.getFirstPassenger() instanceof IControllableFlyingRide listener) {
                listener.setJump(this.down);
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.down);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}
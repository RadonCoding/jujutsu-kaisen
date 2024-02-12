package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;

import java.util.function.Supplier;

public class RightClickInputListenerC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "right_click_input_listener_serverbound");

    private final boolean down;

    public RightClickInputListenerC2SPacket(boolean down) {
        this.down = down;
    }

    public RightClickInputListenerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void handle(ConfigurationPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.getVehicle() instanceof IRightClickInputListener listener) {
                listener.setDown(this.down);
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
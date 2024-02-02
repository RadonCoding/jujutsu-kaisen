package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;

import java.util.function.Supplier;

public class JumpInputListenerC2SPacket {
    private final boolean down;

    public JumpInputListenerC2SPacket(boolean down) {
        this.down = down;
    }

    public JumpInputListenerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.down);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            if (sender.getVehicle() instanceof IJumpInputListener listener) {
                listener.setJump(this.down);
            } else if (sender.getFirstPassenger() instanceof IJumpInputListener listener) {
                listener.setJump(this.down);
            }
        });
        ctx.setPacketHandled(true);
    }
}
package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;

import java.util.function.Supplier;

public class RightClickInputListenerC2SPacket {
    private final boolean down;

    public RightClickInputListenerC2SPacket(boolean down) {
        this.down = down;
    }

    public RightClickInputListenerC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.down);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            if (sender.getVehicle() instanceof IRightClickInputListener listener) {
                listener.setDown(this.down);
            }
        });
        ctx.setPacketHandled(true);
    }
}
package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;

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

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            if (player.getVehicle() instanceof IJumpInputListener listener) {
                listener.setJump(this.down);
            }
        });
        ctx.setPacketHandled(true);
    }
}
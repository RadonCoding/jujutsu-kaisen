package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

import java.util.function.Supplier;

public class ChangeOutputC2SPacket {
    public static final int INCREASE = 1;
    public static final int DECREASE = -1;

    private final int direction;

    public ChangeOutputC2SPacket(int direction) {
        this.direction = direction;
    }

    public ChangeOutputC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.direction);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (this.direction == INCREASE) {
                cap.increaseOutput();
            } else {
                cap.decreaseOutput();
            }
        });
        ctx.setPacketHandled(true);
    }
}
package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public class SetFrequencyC2SPacket {
    private final int frequency;

    public SetFrequencyC2SPacket(int frequency) {
        this.frequency = frequency;
    }

    public SetFrequencyC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.frequency);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (sender.containerMenu instanceof VeilRodMenu menu) {
                if (!menu.stillValid(sender)) {
                    return;
                }
                menu.setFrequency(this.frequency);
            }
        });
        ctx.setPacketHandled(true);
    }
}
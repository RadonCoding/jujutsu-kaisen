package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class SetDomainSizeC2SPacket {
    private final float domainSize;

    public SetDomainSizeC2SPacket(float domainSize) {
        this.domainSize = domainSize;
    }

    public SetDomainSizeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.domainSize);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            sender.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    cap.setDomainSize(Mth.clamp(this.domainSize, 0.5F, 1.5F)));
        });
        ctx.setPacketHandled(true);
    }
}
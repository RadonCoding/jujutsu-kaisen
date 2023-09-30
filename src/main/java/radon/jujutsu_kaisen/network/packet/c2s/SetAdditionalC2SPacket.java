package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.function.Supplier;

public class SetAdditionalC2SPacket {
    private final CursedTechnique technique;

    public SetAdditionalC2SPacket(CursedTechnique technique) {
        this.technique = technique;
    }

    public SetAdditionalC2SPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(CursedTechnique.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.technique);
    }

    public void handle(CustomPayloadEvent.Context ctx) {

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (!sender.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getCopied().contains(this.technique)) {
                cap.setCurrentCopied(this.technique);
            }
        });
        ctx.setPacketHandled(true);
    }
}
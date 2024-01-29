package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.function.Supplier;

public class SetAbsorbedC2SPacket {
    private final CursedTechnique technique;

    public SetAbsorbedC2SPacket(CursedTechnique technique) {
        this.technique = technique;
    }

    public SetAbsorbedC2SPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(CursedTechnique.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.technique);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (!sender.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getAbsorbed().contains(this.technique)) {
                cap.setCurrentAbsorbed(this.technique);
            }
        });
        ctx.setPacketHandled(true);
    }
}
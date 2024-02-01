package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.function.Supplier;

public class SetAdditionalC2SPacket {
    private final ICursedTechnique technique;

    public SetAdditionalC2SPacket(ICursedTechnique technique) {
        this.technique = technique;
    }

    public SetAdditionalC2SPacket(FriendlyByteBuf buf) {
        this(JJKCursedTechniques.getValue(buf.readResourceLocation()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(JJKCursedTechniques.getKey(this.technique));
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            if (!sender.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getCopied().contains(this.technique)) {
                cap.setCurrentCopied(this.technique);
            }
        });
        ctx.setPacketHandled(true);
    }
}
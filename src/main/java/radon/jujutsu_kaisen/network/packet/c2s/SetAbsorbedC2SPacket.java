package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.function.Supplier;

public class SetAbsorbedC2SPacket {
    private final ICursedTechnique technique;

    public SetAbsorbedC2SPacket(ICursedTechnique technique) {
        this.technique = technique;
    }

    public SetAbsorbedC2SPacket(FriendlyByteBuf buf) {
        this(JJKCursedTechniques.getValue(buf.readResourceLocation()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(JJKCursedTechniques.getKey(this.technique));
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            ICurseManipulationData cap = sender.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

            if (JJKAbilities.hasActiveTechnique(sender, JJKCursedTechniques.CURSE_MANIPULATION.get()) && cap.getAbsorbed().contains(this.technique)) {
                cap.setCurrentAbsorbed(this.technique);
            }
        });
        ctx.setPacketHandled(true);
    }
}